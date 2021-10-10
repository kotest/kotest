package io.kotest.engine.reporter

import io.kotest.core.config.configuration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType
import io.kotest.engine.teamcity.Locations
import io.kotest.engine.teamcity.TeamCityMessageBuilder
import io.kotest.engine.test.names.getDisplayNameFormatter
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class TeamCityConsoleReporter(private val prefix: String = TeamCityMessageBuilder.TeamCityPrefix) : Reporter {

   private val formatter = getDisplayNameFormatter(configuration)

   private var errors = false
   private val started = ConcurrentHashMap.newKeySet<KClass<*>>()

   private fun locationHint(testCase: TestCase) =
      Locations.locationHint(testCase.spec.javaClass.canonicalName, testCase.source.lineNumber)

   // intellij has no support for failed suites, so if a container or spec fails we must insert
   // a dummy "test" in order to show something is red or yellow.
   private fun insertDummyFailure(desc: Descriptor, t: Throwable?) {
      val initName = desc.id.value + " <error>"
      println(TeamCityMessageBuilder.testStarted(prefix, initName).build())
      // we must print out the stack trace in between the dummy, so it appears when you click on the test name
      if (t != null) printStackTrace(t)
      val message = t?.message?.let { if (it.lines().size == 1) it else null } ?: "Spec failed"
      println(TeamCityMessageBuilder.testFailed(prefix, initName).message(message).build())
   }

   private fun printStackTrace(t: Throwable) {
      val writer = StringWriter()
      t.printStackTrace(PrintWriter(writer))
      System.err.println()
      System.err.println(writer.toString())
      System.err.flush()
   }

   override fun hasErrors(): Boolean = errors

   override fun specStarted(kclass: KClass<*>) {
      start(kclass)
   }

   private fun start(kclass: KClass<*>) {
      println()
      println(
         TeamCityMessageBuilder
            .testSuiteStarted(prefix, formatter.format(kclass))
            .locationHint(Locations.locationHint(kclass))
            .id(kclass.toDescriptor().id.value)
            .spec()
            .build()
      )
      started.add(kclass)
   }

   override fun specFinished(kclass: KClass<*>, t: Throwable?, results: Map<TestCase, TestResult>) {

      if (!started.contains(kclass))
         start(kclass)

      println()
      val desc = kclass.toDescriptor()
      if (t == null) {
         println(
            TeamCityMessageBuilder
               .testSuiteFinished(prefix, formatter.format(kclass))
               .id(desc.id.value)
               .spec()
               .resultStatus(TestStatus.Success.name)
               .build()
         )
      } else {
         errors = true
         insertDummyFailure(desc, t)
         println(
            TeamCityMessageBuilder
               .testSuiteFinished(prefix, formatter.format(kclass))
               .id(desc.id.value)
               .spec()
               .resultStatus(TestStatus.Failure.name)
               .build()
         )
      }
   }

   override fun testStarted(testCase: TestCase) {
      if (testCase.type == TestType.Container) {
         println()
         println(
            TeamCityMessageBuilder
               .testSuiteStarted(prefix, formatter.format(testCase))
               .id(testCase.descriptor.id.value)
               .parent(testCase.descriptor.parent.id.value)
               .locationHint(locationHint(testCase))
               .testType(testCase.type.name)
               .build()
         )
      } else {
         println()
         println(
            TeamCityMessageBuilder
               .testStarted(prefix, formatter.format(testCase))
               .id(testCase.descriptor.id.value)
               .parent(testCase.descriptor.parent.id.value)
               .locationHint(locationHint(testCase))
               .testType(testCase.type.name)
               .build()
         )
      }
   }

   override fun engineStarted(classes: List<KClass<*>>) {}

   override fun engineFinished(t: List<Throwable>) {
      if (t.isNotEmpty()) {
         println()
         println(TeamCityMessageBuilder.testStarted(prefix, "Test failure").build())
         println()
         val errors = t.joinToString("\n") { t.toString() }
         println(TeamCityMessageBuilder.testFailed(prefix, "Test failure").message(errors).build())
      }
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      val desc = testCase.descriptor
      println()
      when (result.status) {
         TestStatus.Failure, TestStatus.Error -> {
            errors = true
            when (testCase.type) {
               TestType.Container -> {
                  insertDummyFailure(desc, result.error)
                  println(
                     TeamCityMessageBuilder
                        .testSuiteFinished(prefix, formatter.format(testCase))
                        .id(desc.id.value)
                        .parent(testCase.descriptor.parent.id.value)
                        .duration(result.duration)
                        .testType(testCase.type.name)
                        .resultStatus(result.status.name)
                        .build()
                  )
               }
               TestType.Test -> {
                  println(
                     TeamCityMessageBuilder
                        .testFailed(prefix, formatter.format(testCase))
                        .withException(result.error)
                        .id(desc.id.value)
                        .parent(testCase.descriptor.parent.id.value)
                        .duration(result.duration)
                        .testType(testCase.type.name)
                        .resultStatus(result.status.name)
                        .build()
                  )
               }
            }
         }
         TestStatus.Ignored -> {
            val msg = when (testCase.type) {
               TestType.Container ->
                  TeamCityMessageBuilder
                     .testSuiteFinished(prefix, formatter.format(testCase))
                     .id(desc.id.value)
                     .parent(testCase.descriptor.parent.id.value)
                     .testType(testCase.type.name)
                     .resultStatus(result.status.name)
                     .build()
               TestType.Test ->
                  TeamCityMessageBuilder
                     .testIgnored(prefix, formatter.format(testCase))
                     .id(desc.id.value)
                     .parent(testCase.descriptor.parent.id.value)
                     .message(result.reason ?: "No reason")
                     .testType(testCase.type.name)
                     .resultStatus(result.status.name)
                     .build()
            }
            println(msg)
         }
         TestStatus.Success -> {
            val msg = when (testCase.type) {
               TestType.Container ->
                  TeamCityMessageBuilder
                     .testSuiteFinished(prefix, formatter.format(testCase))
                     .id(desc.id.value)
                     .parent(testCase.descriptor.parent.id.value)
                     .duration(result.duration)
                     .testType(testCase.type.name)
                     .resultStatus(result.status.name)
                     .build()
               TestType.Test ->
                  TeamCityMessageBuilder
                     .testFinished(prefix, formatter.format(testCase))
                     .id(desc.id.value)
                     .parent(testCase.descriptor.parent.id.value)
                     .duration(result.duration)
                     .testType(testCase.type.name)
                     .resultStatus(result.status.name)
                     .build()
            }
            println(msg)
         }
      }
   }
}
