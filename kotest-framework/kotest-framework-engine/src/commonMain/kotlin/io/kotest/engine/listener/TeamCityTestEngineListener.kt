package io.kotest.engine.listener

import io.kotest.core.config.Configuration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.DisplayNameFormatter
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.isRootTest
import io.kotest.engine.errors.ExtensionExceptionExtractor
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.teamcity.Locations
import io.kotest.engine.teamcity.TeamCityMessageBuilder
import io.kotest.engine.test.names.DefaultDisplayNameFormatter
import io.kotest.engine.test.names.getDisplayNameFormatter
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * A [TestEngineListener] that logs events to the console using a [TeamCityMessageBuilder].
 */
class TeamCityTestEngineListener(
   private val prefix: String = TeamCityMessageBuilder.TeamCityPrefix
) : TestEngineListener {

   private var formatter: DisplayNameFormatter = DefaultDisplayNameFormatter(Configuration())

   // these are the specs that have been started and the test started event sent to team city
   private val started = mutableSetOf<KClass<*>>()

   private val rootTests = mutableListOf<TestCase>()

   // once a spec has completed, we want to be able to check whether any given test is
   // a container or a leaf test, and so this map contains all test that have children
   private val children = mutableMapOf<Descriptor, MutableList<TestCase>>()

   private val results = mutableMapOf<Descriptor, TestResult>()

   // intellij has no method for failed suites, so if a container or spec fails we must insert
   // a dummy "test" in order to tag the error against that
   private fun insertPlaceholder(t: Throwable, parent: Descriptor) {

      val (name, cause) = ExtensionExceptionExtractor.resolve(t)

      val msg1 = TeamCityMessageBuilder
         .testStarted(prefix, name)
         .id(name)
         .parent(parent.path().value)
         .build()
      println(msg1)

      // we must print out the stack trace in between the dummy, so it appears when you click on the test name
      //t?.printStackTrace()

      val msg2 = TeamCityMessageBuilder
         .testFailed(prefix, name)
         .id(name)
         .parent(parent.path().value)
         .withException(cause)
         .build()
      println(msg2)

      val msg3 = TeamCityMessageBuilder
         .testFinished(prefix, name)
         .id(name)
         .parent(parent.path().value)
         .build()
      println(msg3)
   }

   override suspend fun engineStarted() {}

   override suspend fun engineInitialized(context: EngineContext) {
      formatter = getDisplayNameFormatter(context.configuration.registry(), context.configuration)
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      if (t.isNotEmpty()) {
         println(TeamCityMessageBuilder.testStarted(prefix, "Engine failure").build())
         val errors = t.joinToString("\n") { it.message ?: t::class.bestName() }
         println(TeamCityMessageBuilder.testFailed(prefix, "Engine failure").message(errors).build())
         //println(TeamCityMessageBuilder.testFinished(prefix, "Engine failure").build())
      }
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      // we can output the spec name immediately so there is some feedback that something is happening
      // but all tests will only be output later once we have the full tree
      startSpec(kclass)
   }

   private fun startSpec(kclass: KClass<*>) {
      val msg = TeamCityMessageBuilder
         .testSuiteStarted(prefix, formatter.format(kclass))
         .id(kclass.toDescriptor().path().value)
         // note use location for tests, location hint for test suites
         .locationHint(Locations.location(kclass))
         .build()
      println(msg)
      started.add(kclass)
   }

   private fun finishSpec(kclass: KClass<*>) {
      val msg = TeamCityMessageBuilder
         .testSuiteFinished(prefix, formatter.format(kclass))
         .id(kclass.toDescriptor().path().value)
         // note use location for tests, location hint for test suites
         .locationHint(Locations.location(kclass))
         .build()
      println(msg)
   }

   // todo remove ??
   override suspend fun specAborted(kclass: KClass<*>, t: Throwable) {}

   override suspend fun specFinished(kclass: KClass<*>, results: Map<TestCase, TestResult>) {}

   override suspend fun specExit(kclass: KClass<*>, t: Throwable?) {

      // we must start the test if it wasn't already started
      if (!started.contains(kclass))
         startSpec(kclass)

      // start by outputting each root test and then any nested children
      rootTests.forEach { handleTest(it) }

      // if the spec itself has an error, we must insert a placeholder test
      when (t) {
         null -> Unit
         is MultipleExceptions -> t.causes.forEach { insertPlaceholder(it, kclass.toDescriptor()) }
         else -> insertPlaceholder(t, kclass.toDescriptor())
      }

      finishSpec(kclass)
   }

   private fun handleTest(testCase: TestCase) {
      val result = results[testCase.descriptor] ?: return
      when (result) {
         is TestResult.Ignored -> ignoreTest(testCase, result)
         else -> {
            val nestedTests = children[testCase.descriptor] ?: emptyList()
            if (nestedTests.isEmpty()) {
               startTest(testCase)
               if (result.isErrorOrFailure) failTest(testCase, result)
               finishTest(testCase, result)
            } else {
               startTestSuite(testCase)
               nestedTests.forEach { handleTest(it) }
               // test suites cannot be in a failed state, so we must insert a placeholder to hold any error
               when (val t = result.errorOrNull) {
                  null -> Unit
                  is MultipleExceptions -> t.causes.forEach { insertPlaceholder(it, testCase.descriptor) }
                  else -> insertPlaceholder(t, testCase.descriptor)
               }
               finishTestSuite(testCase, result)
            }
         }
      }
   }

   // ignored specs are completely hidden from output
   override suspend fun specIgnored(kclass: KClass<*>) {}

   // inactive specs are included in the output with a placeholder ignored tests in order that
   // intellij shows the spec as "ignored" (it won't if the test suite is just empty)
   override suspend fun specInactive(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
      if (results.isEmpty()) {
         startSpec(kclass)
         val msg = TeamCityMessageBuilder
            .testIgnored(prefix, "<no tests>")
            .id("<no tests>")
            .parent(kclass.toDescriptor().path().value)
            .result(TestResult.Ignored)
            .build()
         println(msg)
      } else {
         startSpec(kclass)
         results.forEach { (testCase, result) ->
            testIgnored(testCase, if (result is TestResult.Ignored) result.reason else null)
         }
      }
   }

   override suspend fun testStarted(testCase: TestCase) {
      if (testCase.isRootTest()) rootTests.add(testCase)
      else addChild(testCase)
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      if (testCase.isRootTest()) rootTests.add(testCase)
      else addChild(testCase)
   }

   private fun addChild(testCase: TestCase) {
      children.getOrPut(testCase.descriptor.parent) { mutableListOf() }.add(testCase)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      results[testCase.descriptor] = result
   }

   /**
    * For a given [TestCase] will output the "test ignored" message.
    */
   private fun ignoreTest(testCase: TestCase, result: TestResult.Ignored) {
      val msg = TeamCityMessageBuilder
         .testIgnored(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         // note use location for tests, location hint for test suites
         .location(Locations.location(testCase.source))
         .message(result.reason)
         .result(result)
         .build()
      println(msg)
   }

   /**
    * For a given [TestCase] will output the "test started" message.
    */
   private fun startTest(testCase: TestCase) {
      val msg = TeamCityMessageBuilder
         .testStarted(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         // note use location for tests, location hint for test suites
         .location(Locations.location(testCase.source))
         .build()
      println(msg)
   }

   /**
    * For a given [TestCase] will output the "test failed" message.
    */
   private fun failTest(testCase: TestCase, result: TestResult) {
      val msg = TeamCityMessageBuilder
         .testFailed(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         // note use location for tests, location hint for test suites
         .location(Locations.location(testCase.source))
         .withException(result.errorOrNull)
         .result(result)
         .build()
      println(msg)
   }

   /**
    * For a given [TestCase] will output the "test finished" message.
    */
   private fun finishTest(testCase: TestCase, result: TestResult) {
      val msg = TeamCityMessageBuilder
         .testFinished(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         // note use location for tests, location hint for test suites
         .location(Locations.location(testCase.source))
         .result(result)
         .build()
      println(msg)
   }

   /**
    * For a given [TestCase] will output the "test suite started" message.
    */
   private fun startTestSuite(testCase: TestCase) {
      val msg = TeamCityMessageBuilder
         .testSuiteStarted(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         // note use location for tests, location hint for test suites
         .locationHint(Locations.location(testCase.source))
         .build()
      println(msg)
   }

   /**
    * For a given [TestCase] will output the "test suite finished" message.
    */
   private fun finishTestSuite(testCase: TestCase, result: TestResult) {
      val msg = TeamCityMessageBuilder
         .testSuiteFinished(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         // note use location for tests, location hint for test suites
         .locationHint(Locations.location(testCase.source))
         .result(result)
         .build()
      println(msg)
   }
}
