package io.kotest.plugin.intellij.toolwindow

import com.intellij.psi.NavigatablePsiElement
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

object TestExplorerTreeSelectionListener : TreeSelectionListener {
   override fun valueChanged(e: TreeSelectionEvent) {
      when (val node = e.path.node()) {
         is SpecNodeDescriptor -> node.psi.navigate(false)
         is TestNodeDescriptor -> when (node.psi) {
            is NavigatablePsiElement -> node.psi.navigate(false)
         }
      }
   }
}
