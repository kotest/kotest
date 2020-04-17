package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.NoAccessDuringPsiEvents
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import io.kotest.plugin.intellij.psi.specs
import org.jetbrains.kotlin.psi.KtClassOrObject
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeSelectionModel

class TestFileTree(private val project: Project) : com.intellij.ui.treeStructure.Tree() {

   private var file: VirtualFile? = null

   init {
      selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
      showsRootHandles = true
      isRootVisible = false
      cellRenderer = NodeRenderer()
      addTreeSelectionListener(TestExplorerTreeSelectionListener)
   }

   /**
    * Changes the file for this tree and then refreshes the model.
    */
   fun setVirtualFile(file: VirtualFile?) {
      this.file = file
      reloadModel()
   }

   /**
    * Offers the given file. If the file is a test file (contains one or more specs) then accepts it
    * and refreshes the model. Otherwise the existing file (if any) is kept.
    */
   fun offerVirtualFile(file: VirtualFile) {
      ApplicationManager.getApplication().runReadAction {
         val module = ModuleUtilCore.findModuleForFile(file, project)
         if (module != null) {
            val psi = PsiManager.getInstance(project).findFile(file)
            if (DumbService.getInstance(project).isDumb || NoAccessDuringPsiEvents.isInsideEventProcessing()) {
               DumbService.getInstance(project).runWhenSmart {
                  offerVirtualFile(file)
               }
            } else {
               val specs = psi?.specs() ?: emptyList()
               updateSpecs(specs, module, file)
            }
         }
      }
   }

   /**
    * Reloads the model based on the currently set file (if any).
    */
   fun reloadModel() {
      val f = file
      if (f == null) {
         model = noFileModel()
      } else {
         val module = ModuleUtilCore.findModuleForFile(f, project)
         if (module == null) {
            model = noModuleModel()
         } else {
            val psi = PsiManager.getInstance(project).findFile(f)
            if (DumbService.getInstance(project).isDumb || NoAccessDuringPsiEvents.isInsideEventProcessing()) {
               DumbService.getInstance(project).runWhenSmart {
                  offerVirtualFile(f)
               }
            } else {
               val specs = psi?.specs() ?: emptyList()
               updateSpecs(specs, module, f)
            }
         }
      }
   }

   private fun updateSpecs(specs: List<KtClassOrObject>,
                           module: Module,
                           file: VirtualFile) {
      model = createTreeModel(file, project, specs, module)
      expandAllNodes()
   }

   private fun noFileModel(): TreeModel {
      val root = DefaultMutableTreeNode("<no file selected>")
      return DefaultTreeModel(root)
   }

   private fun noModuleModel(): TreeModel {
      val root = DefaultMutableTreeNode("<no module>")
      return DefaultTreeModel(root)
   }
}
