package io.kotest.plugin.intellij.console

import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageParserCallback
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes
import java.text.ParseException

/**
 * An implementation of [ServiceMessageParserCallback] that updates the [console].
 */
class KotestServiceMessageCallback(
   private val console: SMTRunnerConsoleView,
) : ServiceMessageParserCallback {

   // each new proxy must be attached to its parent, so we keep a map of test ids to proxies
   private val proxies = mutableMapOf<String, SMTestProxy>()

   private val root = console.resultsViewer.testsRootNode

   // this is text that was a service message but couldn't be parsed
   override fun parseException(p0: ParseException, p1: String) {
      println("parseException: $p0 $p1")
   }

   // this is text that wasn't a service message, we don't care about this
   override fun regularText(p0: String) {
   }

   @Suppress("UnstableApiUsage")
   override fun serviceMessage(msg: ServiceMessage) {
      println(msg)
      when (msg.messageName) {
         ServiceMessageTypes.TEST_SUITE_STARTED -> {
            val proxy = createProxy(msg = msg, suite = true)
            proxy.setSuiteStarted()
            console.resultsViewer.onSuiteStarted(proxy)
         }
         ServiceMessageTypes.TEST_SUITE_FINISHED -> {
            val proxy = getProxy(msg)
            proxy.setFinished()
            val attrs = MessageAttributeParser.parse(msg)
            attrs.duration?.let { proxy.setDuration(attrs.duration.inWholeMilliseconds) }
            console.resultsViewer.onSuiteFinished(proxy)
         }
         ServiceMessageTypes.TEST_STARTED -> {
            val proxy = createProxy(msg = msg, suite = false)
            proxy.setStarted()
            console.resultsViewer.onTestStarted(proxy)
         }
         ServiceMessageTypes.TEST_FINISHED -> {
            val proxy = getProxy(msg)
            proxy.setFinished()
            val attrs = MessageAttributeParser.parse(msg)
            attrs.duration?.let { proxy.setDuration(attrs.duration.inWholeMilliseconds) }
            console.resultsViewer.onTestFinished(proxy)
         }
         ServiceMessageTypes.TEST_IGNORED -> {
            val proxy = createProxy(msg = msg, suite = false)
            val attrs = MessageAttributeParser.parse(msg)
            proxy.setTestIgnored(attrs.message, null)
            console.resultsViewer.onTestIgnored(proxy)
         }
         ServiceMessageTypes.TEST_FAILED -> {
            println("Not supported $msg")
         }
         else -> {
            println("Unknown message type: ${msg.messageName}")
         }
      }
   }

   private fun createProxy(msg: ServiceMessage, suite: Boolean): SMTestProxy {
      val attrs = MessageAttributeParser.parse(msg)
      val parent = if (attrs.parentId == null) root else proxies[attrs.parentId]
         ?: error("Parent proxy ${attrs.parentId} not found for ${attrs.id} in ${proxies.keys}")
      val proxy = TestProxyBuilder.builder(attrs.name, suite, parent).build()
      proxies[attrs.id] = proxy
      return proxy
   }

   private fun getProxy(msg: ServiceMessage): SMTestProxy {
      val attrs = MessageAttributeParser.parse(msg)
      return proxies[attrs.id] ?: error("Proxy ${attrs.id} not found")
   }
}
