package io.kotest.plugin.intellij.console

import com.intellij.build.BuildViewSettingsProvider
import com.intellij.execution.Platform
import com.intellij.execution.filters.HyperlinkInfo
import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsListener
import com.intellij.execution.testframework.sm.runner.SMTestProxy
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
   internal val publisher: SMTRunnerEventsListener,
) : SMTRunnerConsoleView(consoleProperties, splitterPropertyName), BuildViewSettingsProvider {

   private var lastMessageWasEmptyLine = false

   // each new proxy must be attached to its parent, so we keep a map of test ids to proxies
   // we keep this map here because the console is the object that is passed around by intellij to our callbacks
   private val proxies = mutableMapOf<String, SMTestProxy>()

   override fun isExecutionViewHidden() = false

   override fun printHyperlink(hyperlinkText: String, info: HyperlinkInfo?) {
      println("Hyperlink: $hyperlinkText")
      super.printHyperlink(hyperlinkText, info)
   }

   override fun print(s: String, contentType: ConsoleViewContentType) {
      if (detectUnwantedEmptyLine(s)) return
      super.print(s, contentType)
   }

   internal fun getTestProxy(testId: String): SMTestProxy {
      return proxies[testId] ?: error("Proxy $testId not found")
   }

   internal fun addTestProxy(testId: String, proxy: SMTestProxy) {
      proxies[testId] = proxy
   }

   override fun dispose() {
      proxies.clear()
      super.dispose()
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

