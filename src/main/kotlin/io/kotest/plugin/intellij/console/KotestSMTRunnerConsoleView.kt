package io.kotest.plugin.intellij.console

import com.intellij.build.BuildViewSettingsProvider
import com.intellij.execution.Platform
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.execution.ui.ConsoleViewContentType

/**
 * An [KotestSMTRunnerConsoleView] is a customized [SMTRunnerConsoleView] for ServiceMessage (ie TeamCity format)
 * based test runners. We are extending this to support hiding the gradle build stages view pane.
 */
@Suppress("UnstableApiUsage")
class KotestSMTRunnerConsoleView(
   consoleProperties: KotestSMTRunnerConsoleProperties,
   splitterPropertyName: String,
) : SMTRunnerConsoleView(consoleProperties, splitterPropertyName), BuildViewSettingsProvider {

   private var lastMessageWasEmptyLine = false

   override fun isExecutionViewHidden() = false

   override fun print(s: String, contentType: ConsoleViewContentType) {
      if (detectUnwantedEmptyLine(s)) return
      super.print(s, contentType)
   }

   // IJ test runner events protocol produces many unwanted empty strings
   // this is a workaround to avoid this in the console
   private fun detectUnwantedEmptyLine(s: String): Boolean {
      if (Platform.current().lineSeparator == s) {
         if (lastMessageWasEmptyLine) return true
         lastMessageWasEmptyLine = true
      } else {
         lastMessageWasEmptyLine = false
      }
      return false
   }
}

