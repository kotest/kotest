package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.NoAccessDuringPsiEvents
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import io.kotest.plugin.intellij.psi.isTestFile
import io.kotest.plugin.intellij.psi.specs
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeSelectionModel

class TestFileTree(private val project: Project) : com.intellij.ui.treeStructure.Tree() {

   // the last file set on the editor, might not be the same as the currently selected file
   // because it is only changed as we navigate to test files.
   private var file: VirtualFile? = null

   init {
      selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
      showsRootHandles = true
      isRootVisible = false
      cellRenderer = NodeRenderer()
      // listens to changes in the selections
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
    * Reloads the model based on the currently set file (if any).
    */
   fun reloadModel() {
      when (val f = file) {
         null -> noFileModel()
         else -> reloadModel(f)
      }
   }

   private fun reloadModel(file: VirtualFile, retries: Int = 10) {
      if (!file.isTestFile(project)) {
         model = noFileModel()
      } else {
         val module = ModuleUtilCore.findModuleForFile(file, project) ?: return
         val psi = PsiManager.getInstance(project).findFile(file) ?: return
         if (DumbService.getInstance(project).isDumb || NoAccessDuringPsiEvents.isInsideEventProcessing()) {
            DumbService.getInstance(project).runWhenSmart {
               if (retries > 0)
                  reloadModel(file, retries - 1)
            }
         } else {
            val specs = psi.specs()
            val expanded = isExpanded(0)
            model = createTreeModel(file, project, specs, module)
            expandAllNodes()
            setModuleGroupNodeExpandedState(expanded)
         }
      }
   }

   private fun setModuleGroupNodeExpandedState(expanded: Boolean) {
      if (expanded) expandRow(0) else collapseRow(0)
   }

   private fun noFileModel(): TreeModel {
      val root = DefaultMutableTreeNode("<no test file selected>")
      return DefaultTreeModel(root)
   }
}
