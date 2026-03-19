package io.kotest.plugin.intellij.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.psi.NavigatablePsiElement
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

/**
 * Listens to [TreeSelectionEvent]s which are fired when the user clicks on nodes in
 * the test file tree.
 */
class TestExplorerTreeSelectionListener(
   val project: Project,
) : TreeSelectionListener {
   private val kotestTestExplorerService: KotestTestExplorerService = project.getService(KotestTestExplorerService::class.java)

   override fun valueChanged(e: TreeSelectionEvent) {
      // this event is also fired when the path is "unselected" by clicking inside the editor
      // and isAddedPath will return false for that scenario (which we don't want to react to)
      if (e.isAddedPath && kotestTestExplorerService.autoscrollToSource) {
         val psi = when (val node = e.path.nodeDescriptor()) {
            is SpecNodeDescriptor -> node.psi
            is CallbackNodeDescriptor -> node.psi
            is TestNodeDescriptor -> node.psi
            is IncludeNodeDescriptor -> node.include.psi
            else -> null
         }

         when (psi) {
            is NavigatablePsiElement -> psi.navigate(false)
         }
      }
   }
}
