package io.kotest.plugin.intellij.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.project.Project
import io.kotest.plugin.intellij.actions.RunAction
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
   result.add(RunAction("Run", AllIcons.Actions.Execute, tree, project, "Run"))
   result.add(RunAction("Debug", AllIcons.Actions.StartDebugger, tree, project, "Debug"))
   result.add(RunAction("Run with coverage", AllIcons.General.RunWithCoverage, tree, project, "Coverage"))
   result.addSeparator()
   result.add(ExpandAllAction(tree))
   result.add(CollapseAction(tree))
   result.addSeparator()
   result.add(FilterCallbacksAction(tree))
   result.add(FilterIncludesAction(tree))
   result.add(FilterModulesAction(tree))
   result.add(FilterTagsAction(tree))
   result.addSeparator()
   result.add(NavigateToNodeAction())
   return result
}

class CollapseAction(private val tree: TestFileTree) : AnAction("Collapse All", null, AllIcons.Actions.Collapseall) {
   override fun actionPerformed(e: AnActionEvent) {
      tree.collapseTopLevelNodes()
   }
}

class ExpandAllAction(private val tree: TestFileTree) : AnAction("Expand All", null, AllIcons.Actions.Expandall) {
   override fun actionPerformed(e: AnActionEvent) {
      tree.expandAllNodes()
   }
}

class FilterCallbacksAction(private val tree: TestFileTree) :
   ToggleAction("Filter Vallbacks", null, AllIcons.Nodes.Controller) {

   override fun getActionUpdateThread() = ActionUpdateThread.EDT

   override fun isSelected(e: AnActionEvent): Boolean {
      return TestExplorerState.showCallbacks
   }

   override fun setSelected(e: AnActionEvent, state: Boolean) {
      TestExplorerState.showCallbacks = state
      tree.reloadModel()
   }
}

class FilterModulesAction(private val tree: TestFileTree) :
   ToggleAction("Filter Modules", null, AllIcons.Nodes.ModuleGroup) {

   override fun getActionUpdateThread() = ActionUpdateThread.EDT

   override fun isSelected(e: AnActionEvent): Boolean {
      return TestExplorerState.showModules
   }

   override fun setSelected(e: AnActionEvent, state: Boolean) {
      TestExplorerState.showModules = state
      tree.reloadModel()
   }
}

class FilterTagsAction(private val tree: TestFileTree) : ToggleAction("Filter Tags", null, AllIcons.Nodes.Tag) {
   override fun getActionUpdateThread() = ActionUpdateThread.EDT

   override fun isSelected(e: AnActionEvent): Boolean {
      return TestExplorerState.showTags
   }

   override fun setSelected(e: AnActionEvent, state: Boolean) {
      TestExplorerState.showTags = state
      tree.reloadModel()
   }
}

class FilterIncludesAction(private val tree: TestFileTree) : ToggleAction("Filter Includes", null, AllIcons.Nodes.Tag) {
   override fun getActionUpdateThread() = ActionUpdateThread.EDT

   override fun isSelected(e: AnActionEvent): Boolean {
      return TestExplorerState.showIncludes
   }

   override fun setSelected(e: AnActionEvent, state: Boolean) {
      TestExplorerState.showIncludes = state
      tree.reloadModel()
   }
}

class NavigateToNodeAction : ToggleAction(
   "Autoscroll To Source",
   null,
   AllIcons.General.AutoscrollToSource
) {
   override fun getActionUpdateThread() = ActionUpdateThread.EDT

   override fun isSelected(e: AnActionEvent): Boolean {
      return TestExplorerState.autoscrollToSource
   }

   override fun setSelected(e: AnActionEvent, state: Boolean) {
      TestExplorerState.autoscrollToSource = state
   }
}
