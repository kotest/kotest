package io.kotest.plugin.intellij.actions

import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.RunManager
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import io.kotest.plugin.intellij.run.idea.KotestConfigurationFactory
import io.kotest.plugin.intellij.run.idea.KotestConfigurationType
import io.kotest.plugin.intellij.run.idea.KotestRunConfiguration
import io.kotest.plugin.intellij.run.idea.generateName
import io.kotest.plugin.intellij.toolwindow.ModuleNodeDescriptor
import io.kotest.plugin.intellij.toolwindow.SpecNodeDescriptor
import io.kotest.plugin.intellij.toolwindow.TestNodeDescriptor
import io.kotest.plugin.intellij.toolwindow.nodeDescriptor
import javax.swing.Icon
import javax.swing.JTree

class RunAction(text: String,
                icon: Icon,
                private val tree: JTree,
                private val project: Project,
                private val executorId: String) : AnAction(text, null, icon) {

   override fun actionPerformed(e: AnActionEvent) {
      runNode(tree, project, executorId, true)
   }

   override fun update(e: AnActionEvent) {
      if (e.isFromActionToolbar) {
         e.presentation.isEnabled = when (tree.selectionPath?.nodeDescriptor()) {
            is SpecNodeDescriptor -> true
            is TestNodeDescriptor -> true
            is ModuleNodeDescriptor -> true
            else -> false
         }
      }
   }

   override fun getActionUpdateThread(): ActionUpdateThread {
      return ActionUpdateThread.BGT
   }
}

fun runNode(tree: JTree, project: Project, executorId: String, executeBranch: Boolean) {
   val path = tree.selectionPath
   if (path != null) {
      when (val node = path.nodeDescriptor()) {
         is SpecNodeDescriptor -> if (executeBranch) runSpec(node, project, executorId)
         is TestNodeDescriptor -> if (executeBranch || node.test.nestedTests.isEmpty()) runTest(node, project, executorId)
         is ModuleNodeDescriptor -> runModule(node.module, executorId)
      }
   }
}

fun runTest(node: TestNodeDescriptor, project: Project, executorId: String) {
   val manager = RunManager.getInstance(project)
   val executor = ExecutorRegistry.getInstance().getExecutorById(executorId)!!

   val name = node.test.test.name.displayName()
   val config = manager.createConfiguration(name, KotestConfigurationFactory(KotestConfigurationType()))
   val run = config.configuration as KotestRunConfiguration

   run.setTestPath(node.test.test.testPath())
   run.setSpecName(node.spec.fqn.asString())
   run.setModule(node.module)
   run.name = generateName(node.spec.fqn, node.test.test)

   manager.addConfiguration(config)
   manager.selectedConfiguration = config
   ExecutionUtil.runConfiguration(config, executor)
}

fun runSpec(node: SpecNodeDescriptor, project: Project, executorId: String) {
   val manager = RunManager.getInstance(project)
   val executor = ExecutorRegistry.getInstance().getExecutorById(executorId)!!

   val name = node.fqn.shortName().asString()
   val config = manager.createConfiguration(name, KotestConfigurationFactory(KotestConfigurationType()))
   val run = config.configuration as KotestRunConfiguration

   run.setTestPath(null)
   run.setSpecName(node.fqn.asString())
   run.setModule(node.module)
   run.name = generateName(node.fqn, null)

   manager.addConfiguration(config)
   manager.selectedConfiguration = config
   ExecutionUtil.runConfiguration(config, executor)
}

fun runModule(module: Module, executorId: String) {
   val name = "Run all in ${module.name}"

   val manager = RunManager.getInstance(module.project)
   val executor = ExecutorRegistry.getInstance().getExecutorById(executorId)!!

   val config = manager.createConfiguration(name, KotestConfigurationFactory(KotestConfigurationType()))
   val run = config.configuration as KotestRunConfiguration

   run.setTestPath(null)
   run.setSpecName(null)
   run.setModule(module)
   run.name = name

   manager.addConfiguration(config)
   manager.selectedConfiguration = config
   ExecutionUtil.runConfiguration(config, executor)
}
