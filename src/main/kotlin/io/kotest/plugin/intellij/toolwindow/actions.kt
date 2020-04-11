package io.kotest.plugin.intellij.toolwindow

import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.RunManager
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import io.kotest.plugin.intellij.KotestConfigurationFactory
import io.kotest.plugin.intellij.KotestConfigurationType
import io.kotest.plugin.intellij.KotestRunConfiguration
import javax.swing.Icon
import javax.swing.JTree

class RunAction(icon: Icon,
                private val tree: JTree,
                private val project: Project,
                private val executorId: String) : AnAction(icon) {
   override fun actionPerformed(e: AnActionEvent) {
      println(e)
      runTest(tree, project, executorId, true)
   }
}

fun createRunActions(tree: JTree, project: Project): List<RunAction> {
   return listOf(
      RunAction(AllIcons.Actions.Execute, tree, project, "Run"),
      RunAction(AllIcons.Actions.StartDebugger, tree, project, "Debug"),
      RunAction(AllIcons.General.RunWithCoverage, tree, project, "Coverage")
   )
}

fun runTest(tree: JTree, project: Project, executorId: String, executeBranch: Boolean) {
   val path = tree.selectionPath
   if (path != null) {
      when (val node = path.node()) {
         is SpecNodeDescriptor -> if (executeBranch) runSpec(node, project, executorId)
         is TestNodeDescriptor -> if (executeBranch || node.test.tests.isEmpty()) runTest(node, project, executorId)
      }
   }
}

fun runTest(node: TestNodeDescriptor, project: Project, executorId: String) {

   val manager = RunManager.getInstance(project)
   val executor = ExecutorRegistry.getInstance().getExecutorById(executorId)

   val name = node.test.test.name
   val config = manager.createConfiguration(name, KotestConfigurationFactory(KotestConfigurationType))
   val run = config.configuration as KotestRunConfiguration

   run.setTestName(node.test.test.path)
   run.setSpecName(node.spec.fqn.asString())
   run.setModule(node.module)
   run.setGeneratedName()

   manager.addConfiguration(config)
   manager.selectedConfiguration = config
   ExecutionUtil.runConfiguration(config, executor)
}

fun runSpec(node: SpecNodeDescriptor, project: Project, executorId: String) {

   val manager = RunManager.getInstance(project)
   val executor = ExecutorRegistry.getInstance().getExecutorById(executorId)

   val name = node.fqn.shortName().asString()
   val config = manager.createConfiguration(name, KotestConfigurationFactory(KotestConfigurationType))
   val run = config.configuration as KotestRunConfiguration

   run.setTestName("")
   run.setSpecName(node.fqn.asString())
   run.setModule(node.module)
   run.setGeneratedName()

   manager.addConfiguration(config)
   manager.selectedConfiguration = config
   ExecutionUtil.runConfiguration(config, executor)
}
