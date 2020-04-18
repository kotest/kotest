package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

fun JTree.expandAllNodes() = expandAllNodes(0, rowCount)

fun JTree.expandAllNodes(startingIndex: Int, rowCount: Int) {
   for (i in startingIndex until rowCount) {
      expandRow(i)
   }
   if (getRowCount() != rowCount) {
      expandAllNodes(rowCount, getRowCount())
   }
}

fun JTree.collapseTopLevelNodes() {
   val root = model.root as DefaultMutableTreeNode
   for (node in root.children().toList() as List<DefaultMutableTreeNode>) {
      val path = TreePath(node.path)
      this.collapsePath(path)
   }
}

fun TreePath.nodeDescriptor(): PresentableNodeDescriptor<*>? {
   return when (val last = lastPathComponent) {
      is DefaultMutableTreeNode -> when (val obj = last.userObject) {
         is PresentableNodeDescriptor<*> -> obj
         else -> null
      }
      else -> null
   }
}
