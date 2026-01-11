package io.kotest.plugin.intellij.locations

import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsAdapter
import com.intellij.execution.testframework.sm.runner.SMTestProxy

/**
 * Listens to SMTest events and updates the [SMTestProxy] if the test name is using
 * the special Kotest format for jump-to-source support on tests.
 */
internal class KotestSMTRunnerEventsAdapter : SMTRunnerEventsAdapter() {

   override fun onSuiteStarted(suite: SMTestProxy) {
      handleKotestLocator(suite)
   }

   override fun onTestStarted(test: SMTestProxy) {
      handleKotestLocator(test)
   }

   private fun handleKotestLocator(proxy: SMTestProxy) {
      // attempt to parse out the location from the test name
      val location = KotestLocationParser.parse(proxy.name)
      // if we have the special Kotest element, then we will reset the presentable name and use our
      // own test locator to allow for jump-to-source support
      if (location != null) {
         proxy.locator = KotestLocationTestLocator(location)
         proxy.setPresentableName(location.presentableName)
      }
   }
}

internal object KotestLocationParser {

   private val regex = "<kotest>(.*)</kotest>(.*)".toRegex()

   fun parse(name: String): KotestLocation? {
      val result = regex.find(name) ?: return null
      return KotestLocation(result.groupValues[1], result.groupValues[2])
   }
}

internal data class KotestLocation(val path: String, val presentableName: String)
