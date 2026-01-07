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

fun createToolbar(
   toolbarOwner: JComponent,
   tree: TestFileTree,
   project: Project,
): JComponent {
   val actionManager = ActionManager.getInstance()
   val toolbar =
      actionManager.createActionToolbar(
         ActionPlaces.STRUCTURE_VIEW_TOOLBAR,
         createActionGroup(tree, project),
         true,
      )
   toolbar.targetComponent = toolbarOwner
   return toolbar.component
}

private fun createActionGroup(
   tree: TestFileTree,
   project: Project,
): DefaultActionGroup {
   val result = DefaultActionGroup()
   result.add(RunAction("Run", AllIcons.Actions.Execute, tree, project, "Run"))
   result.add(RunAction("Debug", AllIcons.Actions.StartDebugger, tree, project, "Debug"))
   result.add(RunAction("Run with coverage", AllIcons.General.RunWithCoverage, tree, project, "Coverage"))
   result.addSeparator()
   result.add(ExpandAllAction(tree))
   result.add(CollapseAction(tree))
   result.addSeparator()
   result.add(FilterCallbacksAction(project))
   result.add(FilterIncludesAction(project))
   result.add(FilterModulesAction(project))
   result.add(FilterTagsAction(project))
   result.addSeparator()
   result.add(NavigateToNodeAction(project))
   return result
}

class CollapseAction(
   private val tree: TestFileTree,
) : AnAction("Collapse All", null, AllIcons.Actions.Collapseall) {
   override fun actionPerformed(e: AnActionEvent) {
      tree.collapseTopLevelNodes()
   }
}

class ExpandAllAction(
   private val tree: TestFileTree,
) : AnAction("Expand All", null, AllIcons.Actions.Expandall) {
   override fun actionPerformed(e: AnActionEvent) {
      tree.expandAllNodes()
   }
}

class FilterCallbacksAction(
   project: Project,
) : ToggleAction("Filter Callbacks", null, AllIcons.Nodes.Controller) {
   private val kotestTestExplorerService: KotestTestExplorerService = project.getService(KotestTestExplorerService::class.java)

   override fun getActionUpdateThread() = ActionUpdateThread.EDT

   override fun isSelected(e: AnActionEvent): Boolean = kotestTestExplorerService.showCallbacks

   override fun setSelected(
      e: AnActionEvent,
      state: Boolean,
   ) {
      kotestTestExplorerService.showCallbacks = state
   }
}

class FilterModulesAction(
   project: Project,
) : ToggleAction("Filter Modules", null, AllIcons.Nodes.ModuleGroup) {
   private val kotestTestExplorerService: KotestTestExplorerService = project.getService(KotestTestExplorerService::class.java)

   override fun getActionUpdateThread() = ActionUpdateThread.EDT

   override fun isSelected(e: AnActionEvent): Boolean = kotestTestExplorerService.showModules

   override fun setSelected(
      e: AnActionEvent,
      state: Boolean,
   ) {
      kotestTestExplorerService.showModules = state
   }
}

class FilterTagsAction(
   project: Project,
) : ToggleAction("Filter Tags", null, AllIcons.Nodes.Tag) {
   override fun getActionUpdateThread() = ActionUpdateThread.EDT

   private val kotestTestExplorerService: KotestTestExplorerService = project.getService(KotestTestExplorerService::class.java)

   override fun isSelected(e: AnActionEvent): Boolean = kotestTestExplorerService.showTags

   override fun setSelected(
      e: AnActionEvent,
      state: Boolean,
   ) {
      kotestTestExplorerService.showTags = state
   }
}

class FilterIncludesAction(
   project: Project,
) : ToggleAction("Filter Includes", null, AllIcons.Nodes.Tag) {
   private val kotestTestExplorerService: KotestTestExplorerService = project.getService(KotestTestExplorerService::class.java)

   override fun getActionUpdateThread() = ActionUpdateThread.EDT

   override fun isSelected(e: AnActionEvent): Boolean = kotestTestExplorerService.showIncludes

   override fun setSelected(
      e: AnActionEvent,
      state: Boolean,
   ) {
      kotestTestExplorerService.showIncludes = state
   }
}

class NavigateToNodeAction(
   project: Project,
) : ToggleAction("Autoscroll To Source", null, AllIcons.General.AutoscrollToSource) {
   private val kotestTestExplorerService: KotestTestExplorerService = project.getService(KotestTestExplorerService::class.java)

   override fun getActionUpdateThread() = ActionUpdateThread.EDT

   override fun isSelected(e: AnActionEvent): Boolean = kotestTestExplorerService.autoscrollToSource

   override fun setSelected(
      e: AnActionEvent,
      state: Boolean,
   ) {
      kotestTestExplorerService.autoscrollToSource = state
   }
}
