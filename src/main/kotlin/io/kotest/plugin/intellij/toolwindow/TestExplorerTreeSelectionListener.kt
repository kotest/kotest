package io.kotest.plugin.intellij.toolwindow

import com.intellij.psi.NavigatablePsiElement
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode

object TestExplorerTreeSelectionListener : TreeSelectionListener {
   override fun valueChanged(e: TreeSelectionEvent) {
      when (val last = e.path.lastPathComponent) {
         is DefaultMutableTreeNode -> when (val obj = last.userObject) {
            is SpecNodeDescriptor -> obj.psi.navigate(true)
            is TestNodeDescriptor -> when (obj.psi) {
               is NavigatablePsiElement -> obj.psi.navigate(true)
            }
         }
      }
   }
}
