package io.kotest.plugin.intellij.console

import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsListener
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.externalSystem.execution.ExternalSystemExecutionConsoleManager
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTask
import com.intellij.openapi.externalSystem.service.internal.ExternalSystemExecuteTaskTask
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import io.kotest.plugin.intellij.Constants
import io.kotest.plugin.intellij.gradle.GradleUtils
import jetbrains.buildServer.messages.serviceMessages.ServiceMessagesParser
import org.jetbrains.plugins.gradle.util.GradleConstants

/**
 * An implementation of [ExternalSystemExecutionConsoleManager] that provides a custom [SMTRunnerConsoleView]
 * for displaying Kotest test results when executing Gradle tasks that run Kotest tests.
 */
class KotestExecutionConsoleManager : ExternalSystemExecutionConsoleManager<SMTRunnerConsoleView, ProcessHandler> {

   private val parser = ServiceMessagesParser()

   override fun getExternalSystemId(): ProjectSystemId {
      return GradleConstants.SYSTEM_ID
   }

   /**
    * Provides actions to restart execution task process handled by given console.
    * @param consoleView – is console into which restart actions will be placed.
    */
   override fun getRestartActions(consoleView: SMTRunnerConsoleView): Array<AnAction> {
      return emptyArray()
   }

   @Suppress("UnstableApiUsage", "OverrideOnly")
   override fun attachExecutionConsole(
      project: Project,
      task: ExternalSystemTask,
      env: ExecutionEnvironment?,
      processHandler: ProcessHandler?
   ): SMTRunnerConsoleView? {

      if (env == null) return null
      if (processHandler == null) return null
      val settings = env.runnerAndConfigurationSettings ?: return null

      val consoleProperties = KotestSMTRunnerConsoleProperties(settings.configuration, env.executor)

      val splitterPropertyName = SMTestRunnerConnectionUtil.getSplitterPropertyName(Constants.FRAMEWORK_NAME)
      val publisher = project.messageBus.syncPublisher(SMTRunnerEventsListener.TEST_STATUS)
      val consoleView = KotestSMTRunnerConsoleView(consoleProperties, splitterPropertyName, publisher, project)

      // sets up the process listener on the console view, using the properties that were passed to the console
      SMTestRunnerConnectionUtil.initConsoleView(consoleView, Constants.FRAMEWORK_NAME)

      consoleView.resultsViewer.testsRootNode.executionId = env.executionId
      consoleView.resultsViewer.testsRootNode.setSuiteStarted()

      consoleView.resultsViewer.onSuiteStarted(consoleView.resultsViewer.testsRootNode)
      publisher.onSuiteStarted(consoleView.resultsViewer.testsRootNode)

      processHandler.addProcessListener(object : ProcessAdapter() {
         override fun processTerminated(event: ProcessEvent) {

            consoleView.callback.addNoTestsPlaceholder()

            if (event.exitCode != 0) {
               consoleView.resultsViewer.testsRootNode.setTestFailed("Exit code ${event.exitCode}", null, true)
            } else {
               consoleView.resultsViewer.testsRootNode.setFinished()
            }

            consoleView.resultsViewer.onBeforeTestingFinished(consoleView.resultsViewer.testsRootNode)
            publisher.onBeforeTestingFinished(consoleView.resultsViewer.testsRootNode)

            consoleView.resultsViewer.onTestingFinished(consoleView.resultsViewer.testsRootNode)
            publisher.onTestingFinished(consoleView.resultsViewer.testsRootNode)
         }
      })

      return consoleView
   }

   /**
    * Returns true if this implementation of [ExternalSystemExecutionConsoleManager] should be used to
    * handle the output of the given [task]. We determine true if we are running tests and have the
    * kotest gradle plugin applied to the project
    *
    * This method is invoked for all extensions for each task that is executed by an external system.
    * It is up to this extension to determine if it is applicable for the given task.
    */
   override fun isApplicableFor(task: ExternalSystemTask): Boolean {
      if (task is ExternalSystemExecuteTaskTask) {
         if (task.externalSystemId.id == GradleConstants.SYSTEM_ID.id) {
            println("Checking is applicable, tasks to execute: ${task.tasksToExecute} state=${task.state} data=${task.userDataString} externalPath=${task.externalProjectPath}")
            return GradleUtils.hasKotestTask(task.tasksToExecute)
         }
      }
      return false
   }

   override fun onOutput(
      executionConsole: SMTRunnerConsoleView,
      processHandler: ProcessHandler,
      text: String,
      processOutputType: Key<*>, // is stdout or stderr
   ) {
      when (executionConsole) {
         is KotestSMTRunnerConsoleView -> parser.parse(text, executionConsole.callback)
      }
   }
}
