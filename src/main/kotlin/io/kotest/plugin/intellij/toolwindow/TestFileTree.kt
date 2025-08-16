package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.openapi.project.Project
import com.intellij.ui.TreeUIHelper
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeSelectionModel

class TestFileTree(
   project: Project,
) : com.intellij.ui.treeStructure.Tree(),
   KotestTestExplorerService.ModelListener {

   private val testExplorerTreeSelectionListener = TestExplorerTreeSelectionListener(project)
   private val kotestTestExplorerService: KotestTestExplorerService =
      project.getService(KotestTestExplorerService::class.java)
   private var initialized = false

   init {
      selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
      showsRootHandles = true
      isRootVisible = false
      cellRenderer = NodeRenderer()
      // enable speed search like in the Project tool window, using presentable text
      TreeUIHelper.getInstance().installTreeSpeedSearch(this, { path ->
         val node = path.lastPathComponent as? DefaultMutableTreeNode
         val descriptor = node?.userObject as? PresentableNodeDescriptor<*>
         descriptor?.presentation?.presentableText ?: node?.userObject?.toString() ?: ""
      }, false)
      // listens to changes in the selections
      addTreeSelectionListener(testExplorerTreeSelectionListener)
      kotestTestExplorerService.registerModelListener(this)
      initialized = true
   }

   override fun setModel(treeModel: TreeModel) {
      if (!initialized) {
         super.setModel(treeModel)
         return
      }
      val expanded = isExpanded(0)
      super.setModel(treeModel)
      expandAllNodes()
      setModuleGroupNodeExpandedState(expanded)
   }

   private fun setModuleGroupNodeExpandedState(expanded: Boolean) {
      if (expanded) expandRow(0) else collapseRow(0)
   }
}
