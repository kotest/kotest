package io.kotest.plugin.intellij.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import javax.swing.JComponent
import javax.swing.JTree

fun createToolbar(runActions: List<RunAction>, tree: JTree): JComponent {
   val actionManager = ActionManager.getInstance()
   return actionManager.createActionToolbar(
      ActionPlaces.STRUCTURE_VIEW_TOOLBAR,
      createActionGroup(runActions, tree),
      true
   ).component
}

private fun createActionGroup(actions: List<RunAction>, tree: JTree): DefaultActionGroup {
   val result = DefaultActionGroup()
   actions.forEach { result.add(it) }
   result.addSeparator()
   result.add(ExpandAllAction(tree))
   result.add(CollapseAction(tree))
   return result
}

class CollapseAction(private val tree: JTree) : AnAction(AllIcons.Actions.Collapseall) {
   override fun actionPerformed(e: AnActionEvent) {
      tree.collapseAllNodes()
      // always show the specs at least
      tree.expandRow(0)
   }
}

class ExpandAllAction(private val tree: JTree) : AnAction(AllIcons.Actions.Expandall) {
   override fun actionPerformed(e: AnActionEvent) {
      tree.expandAllNodes()
   }
}
