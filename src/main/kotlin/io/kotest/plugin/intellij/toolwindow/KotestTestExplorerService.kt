package io.kotest.plugin.intellij.toolwindow

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.Service
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.NoAccessDuringPsiEvents
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import io.kotest.plugin.intellij.findFiles
import io.kotest.plugin.intellij.psi.getAllSuperClasses
import io.kotest.plugin.intellij.psi.isTestFile
import io.kotest.plugin.intellij.psi.specs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtProperty
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel
import kotlin.properties.Delegates.observable

/**
 * Manages state related to the test explorer tool window, including:
 *   - Currently selected options in the UI
 *   - The current file being displayed
 *   - The tags found in the project
 */
@Service(Service.Level.PROJECT)
class KotestTestExplorerService(
   private val project: Project,
   private val scope: CoroutineScope,
) {

   // TODO: Remove when dropping IC-223 support.
   @Suppress("unused")
   constructor(project: Project) : this(project, CoroutineScope(Dispatchers.Default))

   var showCallbacks by observable(true) { _, _, _ -> reloadModelInBackgroundThread() }
   var showTags by observable(true) { _, _, _ -> reloadModelInBackgroundThread() }
   var showModules by observable(true) { _, _, _ -> reloadModelInBackgroundThread() }
   var showIncludes by observable(true) { _, _, _ -> reloadModelInBackgroundThread() }
   var autoscrollToSource by observable(true) { _, _, _ -> reloadModelInBackgroundThread() }

   var tags: List<String> by observable(emptyList()) { _, _, _ -> reloadModelInBackgroundThread() }
   var currentFile: VirtualFile? by observable(null) { _, _, _ -> reloadModelInBackgroundThread() }

   /**
    * Interface used by dependent components to receive updates to the tree model.
    */
   interface ModelListener {
      fun setModel(treeModel: TreeModel)
   }

   private val modelListeners = mutableListOf<ModelListener>()
   fun registerModelListener(modelListener: ModelListener) { modelListeners.add(modelListener) }

   private fun reloadModelInBackgroundThread() {
      scope.launch(Dispatchers.Default) {
         // TODO: Just use runReadAction function after dropping IC-223
         ApplicationManager.getApplication().runReadAction {
            reloadModel(currentFile)
         }
      }
   }

   private fun reloadModel(
      file: VirtualFile?,
      retries: Int = 10,
   ) {
      if (file == null || !file.isTestFile(project)) {
         broadcastUpdatedModel(noFileModel)
      } else {
         val module = ModuleUtilCore.findModuleForFile(file, project) ?: return
         val psi = PsiManager.getInstance(project).findFile(file) ?: return

         return if (DumbService.getInstance(project).isDumb || NoAccessDuringPsiEvents.isInsideEventProcessing()) {
            DumbService.getInstance(project).runWhenSmart {
               if (retries > 0) {
                  reloadModel(file, retries - 1)
               } else {
                  noFileModel
               }
            }
         } else {
            val specs = psi.specs()
            broadcastUpdatedModel(createTreeModel(file, project, specs, module))
         }
      }
   }

   private fun broadcastUpdatedModel(model: TreeModel) {
      scope.launch(Dispatchers.EDT) {
         modelListeners.forEach { it.setModel(model) }
      }
   }

   fun scanTags() {
      scope.launch(Dispatchers.Default) {
         // TODO: Just use runReadAction function after dropping IC-223
         ApplicationManager.getApplication().runReadAction {
            tags =
               findFiles(project)
                  .mapNotNull { it.toPsiFile(project) }
                  .flatMap { it.detectKotestTags() }
                  .distinct()
                  .sorted()
         }
      }
   }

   /**
    * Looks for Kotest tags in this file, defined at the top level as either vals or anon objects.
    */
   private fun PsiFile.detectKotestTags(): List<String> =
      children.mapNotNull {
         when (it) {
            is KtClassOrObject -> if (it.getAllSuperClasses().contains(TagSuperClass)) it.name else null
            is KtProperty -> it.name
            else -> null
         }
      }

   companion object {
      private val noFileModel = DefaultTreeModel(DefaultMutableTreeNode("<no test file selected>"))
   }
}
