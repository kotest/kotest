package io.kotest.engine.teamcity

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.mpp.Logger
import kotlin.reflect.KClass

/**
 * [TeamCityWriter] handles outputting in the team city format using a [TeamCityMessageBuilder].
 */
internal class TeamCityWriter(
   private val prefix: String,
   private val formatter: FallbackDisplayNameFormatter
) {

   private val logger = Logger(TeamCityWriter::class)

   /**
    * For a given [TestCase] will output the "test ignored" message.
    */
   internal fun outputTestIgnored(testCase: TestCase, result: TestResult.Ignored) {
      val msg = TeamCityMessageBuilder
         .testIgnored(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .locationHint(Locations.location(testCase.source))
         .message(result.reason)
         .result(result)
         .build()
      println(msg)
   }

   /**
    * For a [TestCase] will output the "test started" message.
    */
   internal fun outputTestStarted(testCase: TestCase) {
      logger.log { Pair(testCase.name.testName, "startTest ${testCase.descriptor.path().value}") }
      val msg = TeamCityMessageBuilder
         .testStarted(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .locationHint(Locations.location(testCase.source))
         .build()
      println(msg)
   }

   /**
    * For a [TestCase] will output the "test started" message.
    */
   internal fun outputTestStarted(name: String, parent: String) {
      val msg1 = TeamCityMessageBuilder
         .testStarted(prefix, name)
         .id(name)
         .parent(parent)
         .build()
      println(msg1)
   }

   internal fun outputTestStarted(testName: String) {
      println(TeamCityMessageBuilder.testStarted(prefix, testName).build())
   }

   internal fun outputTestFailed(testName: String, message: String) {
      println(TeamCityMessageBuilder.testFailed(prefix, testName).message(message).build())
   }

   internal fun outputTestFinished(testName: String) {
      println(TeamCityMessageBuilder.testFinished(prefix, testName).build())
   }

   /**
    * For a given [TestCase] will output the "test failed" message.
    */
   internal fun outputTestFailed(testCase: TestCase, result: TestResult, details: Boolean) {
      val msg = TeamCityMessageBuilder
         .testFailed(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         .locationHint(Locations.location(testCase.source))
         .withException(result.errorOrNull, details)
         .result(result)
         .build()
      println(msg)
   }

   /**
    * Outputs the "test failed" message for an arbitrary test name.
    * This is used for placeholder tests.
    */
   internal fun outputTestFailed(name: String, cause: Throwable, details: Boolean, parent: String) {
      val msg2 = TeamCityMessageBuilder
         .testFailed(prefix, name)
         .id(name)
         .parent(parent)
         .withException(cause, details)
         .build()
      println(msg2)
   }

   /**
    * For a given [TestCase] will output the "test finished" message.
    */
   internal fun outputTestFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.testName, "finishTest ${testCase.descriptor.path().value}") }
      val msg = TeamCityMessageBuilder
         .testFinished(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         .locationHint(Locations.location(testCase.source))
         .result(result)
         .build()
      println(msg)
   }

   internal fun outputTestFinished(name: String, parent: String) {
      val msg3 = TeamCityMessageBuilder
         .testFinished(prefix, name)
         .id(name)
         .parent(parent)
         .build()
      println(msg3)
   }

   /**
    * For a given [TestCase] will output the "test suite started" message.
    */
   internal fun outputTestSuiteStarted(testCase: TestCase) {
      logger.log { Pair(testCase.name.testName, "startTestSuite ${testCase.descriptor.path().value}") }
      val msg = TeamCityMessageBuilder
         .testSuiteStarted(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .locationHint(Locations.location(testCase.source))
         .build()
      println(msg)
   }

   /**
    * For a given [TestCase] will output the "test suite finished" message.
    */
   internal fun outputTestSuiteFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.testName, "finishTestSuite ${testCase.descriptor.path().value}") }
      val msg = TeamCityMessageBuilder
         .testSuiteFinished(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         .locationHint(Locations.location(testCase.source))
         .result(result)
         .build()
      println(msg)
   }

   /**
    * For a given [KClass] spec will output the "test suite finished" message.
    */
   internal fun outputTestSuiteFinished(kclass: KClass<*>) {
      val msg = TeamCityMessageBuilder
         .testSuiteFinished(prefix, formatter.format(kclass))
         .id(kclass.toDescriptor().path().value)
         .locationHint(Locations.location(kclass))
         .build()
      println(msg)
   }

   internal fun outputTestSuiteStarted(kclass: KClass<*>) {
      val msg = TeamCityMessageBuilder
         .testSuiteStarted(prefix, formatter.format(kclass))
         .id(kclass.toDescriptor().path().value)
         .locationHint(Locations.location(kclass))
         .build()
      println(msg)
   }
}
