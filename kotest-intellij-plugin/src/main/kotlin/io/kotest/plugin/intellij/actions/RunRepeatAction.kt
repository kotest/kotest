package io.kotest.plugin.intellij.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import io.kotest.plugin.intellij.run.KotestRunState

/**
 * A gutter icon action that asks the user how many times to run the test.
 *
 * The count is stored in [KotestRunState] without triggering a run. The next
 * time the test is run via the standard gutter action, [io.kotest.plugin.intellij.run.gradle.GradleMultiplatformJvmTestTaskRunProducer]
 * will pick up the count, inject KOTEST_INVOCATION_COUNT into the run configuration,
 * and clear the pending count so subsequent runs return to the default invocation count.
 */
class RunRepeatAction : AnAction("Run with repetitions...") {

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
   }
}
