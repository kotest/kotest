package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.NodeDescriptor
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

fun JTree.collapseAllNodes() {
   fun collapseAllNodes(node: DefaultMutableTreeNode) {
      node.children().asSequence().toList().forEach {
         collapseAllNodes(it as DefaultMutableTreeNode)
      }
      val path = TreePath(node.path)
      this.collapsePath(path)
   }
   collapseAllNodes(model.root as DefaultMutableTreeNode)
}

fun TreePath.node(): NodeDescriptor<Any>? {
   return when (val last = lastPathComponent) {
      is DefaultMutableTreeNode -> when (val obj = last.userObject) {
         is SpecNodeDescriptor -> obj
         is TestNodeDescriptor -> obj
         is CallbackNodeDescriptor -> obj
         else -> null
      }
      else -> null
   }
}
