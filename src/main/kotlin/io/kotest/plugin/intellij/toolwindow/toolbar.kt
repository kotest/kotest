package io.kotest.plugin.intellij.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.project.Project
import javax.swing.JComponent

fun createToolbar(tree: TestFileTree, project: Project): JComponent {
   val actionManager = ActionManager.getInstance()
   return actionManager.createActionToolbar(
      ActionPlaces.STRUCTURE_VIEW_TOOLBAR,
      createActionGroup(tree, project),
      true
   ).component
}

private fun createActionGroup(tree: TestFileTree, project: Project): DefaultActionGroup {
   val result = DefaultActionGroup()
   result.add(RunAction(AllIcons.Actions.Execute, tree, project, "Run"))
   result.add(RunAction(AllIcons.Actions.StartDebugger, tree, project, "Debug"))
   result.add(RunAction(AllIcons.General.RunWithCoverage, tree, project, "Coverage"))
   result.addSeparator()
   result.add(ExpandAllAction(tree))
   result.add(CollapseAction(tree))
   result.addSeparator()
   result.add(FilterCallbacksAction(tree))
   result.addSeparator()
   result.add(NavigateToNodeAction())
   return result
}

class CollapseAction(private val tree: TestFileTree) : AnAction("Collapse all", null, AllIcons.Actions.Collapseall) {
   override fun actionPerformed(e: AnActionEvent) {
      tree.collapseAllNodes()
      // always show the specs at least
      tree.expandRow(0)
   }
}

class ExpandAllAction(private val tree: TestFileTree) : AnAction("Expand all", null, AllIcons.Actions.Expandall) {
   override fun actionPerformed(e: AnActionEvent) {
      tree.expandAllNodes()
   }
}

class FilterCallbacksAction(private val tree: TestFileTree) : ToggleAction("Filter callbacks", null, AllIcons.General.Filter) {

   override fun isSelected(e: AnActionEvent): Boolean {
      return TestExplorerState.filterCallbacks
   }

   override fun setSelected(e: AnActionEvent, state: Boolean) {
      TestExplorerState.filterCallbacks = state
      tree.reloadModel()
   }
}

class NavigateToNodeAction : ToggleAction(
   "Autoscroll to source",
   null,
   AllIcons.General.AutoscrollToSource) {

   override fun isSelected(e: AnActionEvent): Boolean {
      return TestExplorerState.autoscrollToSource
   }

   override fun setSelected(e: AnActionEvent, state: Boolean) {
      TestExplorerState.autoscrollToSource = state
   }
}
