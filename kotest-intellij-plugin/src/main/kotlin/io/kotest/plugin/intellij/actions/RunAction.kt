package io.kotest.plugin.intellij.actions

import com.intellij.execution.Executor
import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.PsiLocation
import com.intellij.execution.RunManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.run.RunnerMode
import io.kotest.plugin.intellij.run.RunnerModes
import io.kotest.plugin.intellij.run.gradle.GradleKotestTaskRunProducer
import io.kotest.plugin.intellij.run.gradle.GradleMultiplatformJvmTestTaskRunProducer
import io.kotest.plugin.intellij.run.idea.KotestConfigurationFactory
import io.kotest.plugin.intellij.run.idea.KotestConfigurationType
import io.kotest.plugin.intellij.run.idea.KotestRunConfiguration
import io.kotest.plugin.intellij.run.idea.generateName
import io.kotest.plugin.intellij.toolwindow.ModuleNodeDescriptor
import io.kotest.plugin.intellij.toolwindow.SpecNodeDescriptor
import io.kotest.plugin.intellij.toolwindow.TestNodeDescriptor
import io.kotest.plugin.intellij.toolwindow.nodeDescriptor
import io.kotest.plugin.intellij.util.EnvVarUtil
import org.jetbrains.plugins.gradle.execution.GradleRunConfigurationProducer
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration
import javax.swing.Icon
import javax.swing.JTree

class RunAction(
   text: String,
   icon: Icon,
   private val tree: JTree,
   private val project: Project,
   private val executorId: String
) : AnAction(text, null, icon) {

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
         is TestNodeDescriptor -> if (executeBranch || node.test.nestedTests.isEmpty()) runTest(
            node,
            project,
            executorId
         )

         is ModuleNodeDescriptor -> runModule(node.module, executorId)
      }
   }
}


@Suppress("DEPRECATION")
fun runTest(node: TestNodeDescriptor, project: Project, executorId: String) {
   val executor = ExecutorRegistry.getInstance().getExecutorById(executorId) ?: return

   when (RunnerModes.mode(node.module)) {
      RunnerMode.GRADLE_TEST_TASK -> runGradleTestOrSpec(
         executor = executor,
         producer = GradleMultiplatformJvmTestTaskRunProducer(),
         project = project,
         module = node.module,
         psiElement = node.psi,
      )

      RunnerMode.GRADLE_KOTEST_TASK -> runGradleTestOrSpec(
         executor = executor,
         producer = GradleKotestTaskRunProducer(),
         project = project,
         module = node.module,
         psiElement = node.psi,
      )

      RunnerMode.LEGACY -> {
         val manager = RunManager.getInstance(project)
         val name = node.test.test.name.displayName()
         val config = manager.createConfiguration(name, KotestConfigurationFactory(KotestConfigurationType()))
         val run = config.configuration as KotestRunConfiguration
         run.setTestPath(node.test.test.testPath())
         run.setSpecsName(node.spec.fqn.asString())
         run.setModule(node.module)
         run.name = generateName(node.spec.fqn, node.test.test)
         manager.addConfiguration(config)
         manager.selectedConfiguration = config
         ExecutionUtil.runConfiguration(config, executor)
      }

      else -> return
   }
}

@Suppress("DEPRECATION")
fun runSpec(node: SpecNodeDescriptor, project: Project, executorId: String) {
   val executor = ExecutorRegistry.getInstance().getExecutorById(executorId) ?: return
   // Use nameIdentifier as the PSI anchor so producers can traverse up to find the spec class
   // via enclosingSpec(). The spec class itself is not suitable since enclosingSpec() uses
   // getStrictParentOfType which starts from the parent, not the element itself.
   val specNameElement = node.psi.nameIdentifier ?: return

   when (RunnerModes.mode(node.module)) {
      RunnerMode.GRADLE_TEST_TASK -> runGradleTestOrSpec(
         executor = executor,
         producer = GradleMultiplatformJvmTestTaskRunProducer(),
         project = project,
         module = node.module,
         psiElement = specNameElement,
      )

      RunnerMode.GRADLE_KOTEST_TASK ->
         runGradleTestOrSpec(
            executor = executor,
            producer = GradleKotestTaskRunProducer(),
            project = project,
            module = node.module,
            psiElement = specNameElement,
         )

      RunnerMode.LEGACY -> {
         val manager = RunManager.getInstance(project)
         val name = node.fqn.shortName().asString()
         val config = manager.createConfiguration(name, KotestConfigurationFactory(KotestConfigurationType()))
         val run = config.configuration as KotestRunConfiguration
         run.setTestPath(null)
         run.setSpecsName(node.fqn.asString())
         run.setModule(node.module)
         run.name = generateName(node.fqn, null)
         manager.addConfiguration(config)
         manager.selectedConfiguration = config
         ExecutionUtil.runConfiguration(config, executor)
      }

      else -> return
   }
}

@Suppress("DEPRECATION")
fun runModule(module: Module, executorId: String) {
   val name = "Run all in ${module.name}"
   val project = module.project
   val manager = RunManager.getInstance(project)
   val executor = ExecutorRegistry.getInstance().getExecutorById(executorId) ?: return

   when (RunnerModes.mode(module)) {
      RunnerMode.GRADLE_TEST_TASK -> runGradleModule(
         executor = executor,
         producer = GradleMultiplatformJvmTestTaskRunProducer(),
         manager = manager,
         module = module,
         configName = name,
      )

      RunnerMode.GRADLE_KOTEST_TASK -> runGradleModule(
         executor = executor,
         producer = GradleKotestTaskRunProducer(),
         manager = manager,
         module = module,
         configName = name,
      )

      RunnerMode.LEGACY -> {

         val config = manager.createConfiguration(name, KotestConfigurationFactory(KotestConfigurationType()))
         val run = config.configuration as KotestRunConfiguration
         run.setTestPath(null)
         run.setSpecsName(null)
         run.setModule(module)
         run.name = name
         manager.addConfiguration(config)
         manager.selectedConfiguration = config
         ExecutionUtil.runConfiguration(config, executor)
      }

      else -> return
   }
}

private fun runGradleTestOrSpec(
   executor: Executor,
   producer: GradleRunConfigurationProducer,
   project: Project,
   module: Module,
   psiElement: PsiElement
) {
   val context = ConfigurationContext.createEmptyContextForLocation(
      PsiLocation(project, module, psiElement)
   )
   val configFromContext = producer.createConfigurationFromContext(context) ?: return
   val manager = RunManager.getInstance(project)
   manager.addConfiguration(configFromContext.configurationSettings)
   manager.selectedConfiguration = configFromContext.configurationSettings
   producer.onFirstRun(configFromContext, context) {
      ExecutionUtil.runConfiguration(configFromContext.configurationSettings, executor)
   }
}

private fun runGradleModule(
   executor: Executor,
   producer: GradleRunConfigurationProducer,
   manager: RunManager,
   module: Module,
   configName: String
) {
   val config = manager.createConfiguration(configName, producer.configurationFactory)
   val gradleConfig = config.configuration as GradleRunConfiguration
   gradleConfig.settings.externalProjectPath = ExternalSystemApiUtil.getExternalProjectPath(module) ?: return
   gradleConfig.settings.taskNames = listOf("test")
   gradleConfig.settings.scriptParameters = ""
   gradleConfig.isRunAsTest = true
   EnvVarUtil.setKotestIdeaPlugin(gradleConfig.settings)
   manager.addConfiguration(config)
   manager.selectedConfiguration = config
   ExecutionUtil.runConfiguration(config, executor)
}
