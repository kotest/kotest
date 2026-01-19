package io.kotest.plugin.intellij.locations

import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsAdapter
import com.intellij.execution.testframework.sm.runner.SMTestProxy

/**
 * Listens to SMTest events and updates the [SMTestProxy] if the test name is using
 * the special Kotest format for jump-to-source support on tests.
 */
internal class EmbeddedLocationSMTRunnerEventsAdapter : SMTRunnerEventsAdapter() {

   override fun onSuiteStarted(suite: SMTestProxy) {
      handleKotestLocator(suite)
   }

   override fun onTestStarted(test: SMTestProxy) {
      handleKotestLocator(test)
   }

   override fun onTestIgnored(test: SMTestProxy) {
      handleKotestLocator(test)
   }

   private fun handleKotestLocator(proxy: SMTestProxy) {
      // attempt to parse out an embedded location from the test name
      val location = EmbeddedLocationParser.parse(proxy.name)

      if (location != null) {
         // if we have an embedded location, then we will reset the presentable name and use our
         // EmbeddedLocationTestLocator to allow for jump-to-source support
         proxy.locator = EmbeddedLocationTestLocator(location)
         proxy.setPresentableName(location.presentableName)

      } else if (isJavaSuiteClass(proxy)) {
         // if we have a java:suite locator, for a top level class, this doesn't work for kotlin native, so we can
         // use our own locator which will work for both kmp and jvm
         proxy.locator = MultiplatformJavaSuiteLocator()
      }
   }

   // returns true if a class not a test
   internal fun isJavaSuiteClass(proxy: SMTestProxy): Boolean =
      proxy.locationUrl?.matches("java:suite://[a-zA-Z_.]+".toRegex()) == true
}

internal object EmbeddedLocationParser {

   private val regex = "<kotest>(.*)</kotest>(.*)".toRegex()

   fun parse(name: String): EmbeddedLocation? {
      val result = regex.find(name) ?: return null
      return EmbeddedLocation(result.groupValues[1], result.groupValues[2])
   }
}

internal data class EmbeddedLocation(val path: String, val presentableName: String)
