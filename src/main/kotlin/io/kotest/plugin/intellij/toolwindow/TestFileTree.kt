package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import io.kotest.plugin.intellij.psi.specs
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
    * Offers the given file. If the file is a test file then accepts it
    * and refreshes the model. Otherwise the existing file (if any) is kept.
    */
   fun offerVirtualFile(file: VirtualFile?) {
      val f = file
      if (f != null) {
         val module = ModuleUtilCore.findModuleForFile(f, project)
         if (module != null) {
            DumbService.getInstance(project).runWhenSmart {
               try {
                  val psi = PsiManager.getInstance(project).findFile(f)
                  val specs = psi?.specs() ?: emptyList()
                  if (specs.isNotEmpty()) {
                     model = createTreeModel(f, project, specs, module)
                     expandAllNodes()
                  }
               } catch (e: Throwable) {
               }
            }
         }
      }
   }

   /**
    * Reloads the model based on the currently set file.
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
            DumbService.getInstance(project).runWhenSmart {
               try {
                  val psi = PsiManager.getInstance(project).findFile(f)
                  val specs = psi?.specs() ?: emptyList()
                  model = createTreeModel(f, project, specs, module)
                  expandAllNodes()
               } catch (e: Throwable) {
               }
            }
         }
      }
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
