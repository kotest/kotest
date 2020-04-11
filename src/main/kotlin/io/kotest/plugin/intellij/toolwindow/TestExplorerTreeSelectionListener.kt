package io.kotest.plugin.intellij.toolwindow

import com.intellij.psi.NavigatablePsiElement
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

class TestExplorerTreeSelectionListener(private val runActions: List<RunAction>) : TreeSelectionListener {

   override fun valueChanged(e: TreeSelectionEvent) {

      val runEnabled = when (e.path.node()) {
         is SpecNodeDescriptor -> true
         is TestNodeDescriptor -> true
         else -> false
      }

      runActions.forEach {
         it.templatePresentation.isEnabled = runEnabled
      }

      val psi = when (val node = e.path.node()) {
         is SpecNodeDescriptor -> node.psi
         is CallbackNodeDescriptor -> node.psi
         is TestNodeDescriptor -> node.psi
         else -> null
      }
      when (psi) {
         is NavigatablePsiElement -> psi.navigate(false)
      }
   }
}
