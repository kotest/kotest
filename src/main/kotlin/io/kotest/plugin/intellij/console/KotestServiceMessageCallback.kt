@file:Suppress("UnstableApiUsage", "OverrideOnly")

package io.kotest.plugin.intellij.console

import com.intellij.execution.testframework.sm.runner.SMTestProxy
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageParserCallback
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes
import java.text.ParseException

/**
 * An implementation of [ServiceMessageParserCallback] that updates the given [console].
 */
class KotestServiceMessageCallback(
   private val console: KotestSMTRunnerConsole,
) : ServiceMessageParserCallback {

   fun root(): SMTestProxy.SMRootTestProxy = console.resultsViewer.testsRootNode

   // this is text that was a service message but couldn't be parsed
   override fun parseException(p0: ParseException, p1: String) {
      println("Error parsing test result: $p1")
      console.notifyWarn("Error parsing test result", p0.message ?: "")
   }

   // this is text that wasn't a service message, we don't care about this
   override fun regularText(p0: String) {
      if (p0.isNotBlank())
         println("Regular text: $p0")
   }

   override fun serviceMessage(msg: ServiceMessage) {
      when (msg.messageName) {
         ServiceMessageTypes.TEST_SUITE_STARTED -> {
            val proxy = createProxy(msg = msg, suite = true)
            proxy.setSuiteStarted()
            console.resultsViewer.onSuiteStarted(proxy)
            console.publisher.onSuiteStarted(proxy)
         }
         ServiceMessageTypes.TEST_SUITE_FINISHED -> {
            val proxy = getProxy(msg)
            proxy.setFinished()
            val attrs = MessageAttributeParser.parse(msg)
            attrs.duration?.let { proxy.setDuration(attrs.duration.inWholeMilliseconds) }
            console.resultsViewer.onSuiteFinished(proxy)
            console.publisher.onSuiteFinished(proxy)
         }
         ServiceMessageTypes.TEST_STARTED -> {
            val proxy = createProxy(msg = msg, suite = false)
            proxy.setStarted()
            console.resultsViewer.onTestStarted(proxy)
            console.publisher.onTestStarted(proxy)
         }
         ServiceMessageTypes.TEST_FINISHED -> {
            val proxy = getProxy(msg)
            proxy.setFinished()
            val attrs = MessageAttributeParser.parse(msg)
            attrs.duration?.let { proxy.setDuration(attrs.duration.inWholeMilliseconds) }
            console.resultsViewer.onTestFinished(proxy)
            console.publisher.onTestFinished(proxy)
         }
         ServiceMessageTypes.TEST_IGNORED -> {
            val proxy = createProxy(msg = msg, suite = false)
            val attrs = MessageAttributeParser.parse(msg)
            proxy.setTestIgnored(attrs.message, null)
            console.resultsViewer.onTestIgnored(proxy)
            console.publisher.onTestIgnored(proxy)
         }
         ServiceMessageTypes.TEST_FAILED -> {
            println("Handle test failed message $msg")
            val proxy = getProxy(msg)
            val attrs = MessageAttributeParser.parse(msg)
            proxy.setTestFailed(attrs.message, attrs.details, true)
            console.resultsViewer.onTestFailed(proxy)
            console.publisher.onTestFailed(proxy)
         }
         else -> Unit
      }
   }

   /**
    * Adds a placeholder to the console to indicate that no tests were found (if necessary).
    */
   fun addNoTestsPlaceholder() {
      val proxy = TestProxyBuilder.builder("No tests were found", false, root()).build()
      proxy.setStarted()

      console.resultsViewer.onTestStarted(proxy)
      console.publisher.onTestStarted(proxy)

      proxy.setTestFailed("No tests were found", null, true)
      console.resultsViewer.onTestFailed(proxy)
      console.publisher.onTestFailed(proxy)

      proxy.setFinished()
      console.resultsViewer.onTestFinished(proxy)
      console.publisher.onTestFinished(proxy)
   }

   private fun createProxy(msg: ServiceMessage, suite: Boolean): SMTestProxy {
      val attrs = MessageAttributeParser.parse(msg)
      val parentId = attrs.parentId
      val parent = if (parentId == null) root() else console.getTestProxy(parentId)
      val proxy = TestProxyBuilder.builder(attrs.name, suite, parent)
         .withLocation(attrs.location)
         .build()
      console.addTestProxy(attrs.id, proxy)
      return proxy
   }

   private fun getProxy(msg: ServiceMessage): SMTestProxy {
      val attrs = MessageAttributeParser.parse(msg)
      return console.getTestProxy(attrs.id)
   }
}
