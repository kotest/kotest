package io.kotest.plugin.intellij.actions

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import io.kotest.plugin.intellij.run.KotestRunState

/**
 * A gutter icon action that asks the user how many times to run the test,
 * then triggers the standard Gradle run flow with KOTEST_INVOCATION_COUNT set.
 *
 * The count is stored in [KotestRunState] before delegating to the standard
 * [ExecutorAction], so the producer picks it up during configuration setup
 * without any changes to the execution flow (including the multiplatform task chooser).
 */
class RunRepeatAction : AnAction("Run with Repetitions...") {

   override fun actionPerformed(e: AnActionEvent) {
      val project = e.project ?: return

      val countStr = Messages.showInputDialog(
         project,
         "How many times should the test run?",
         "Run with Repetitions",
         null,
         "1",
         object : InputValidator {
            override fun checkInput(inputString: String) =
               inputString.toIntOrNull()?.let { it > 0 } ?: false

            override fun canClose(inputString: String) = checkInput(inputString)
         }
      ) ?: return

      val count = countStr.toIntOrNull() ?: return
      project.service<KotestRunState>().pendingInvocationCount = count

      // Delegate to the standard Run executor action, this triggers the full normal
      // flow: doSetupConfigurationFromContext (which reads and clears pendingInvocationCount),
      // then onFirstRun (multiplatform task chooser), then execution.
      ExecutorAction.getActions(1).firstOrNull()?.actionPerformed(e)
   }
}
