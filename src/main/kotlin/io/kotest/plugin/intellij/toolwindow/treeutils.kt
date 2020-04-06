package io.kotest.plugin.intellij.toolwindow

import com.intellij.ide.util.treeView.NodeDescriptor
import io.kotest.plugin.intellij.toolwindow.CallbackNodeDescriptor
import io.kotest.plugin.intellij.toolwindow.SpecNodeDescriptor
import io.kotest.plugin.intellij.toolwindow.TestNodeDescriptor
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
