package io.kotest.engine.listener

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.engine.test.names.formatTestPath
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

class BasicConsoleTestEngineListener : AbstractTestEngineListener() {

   private var errors = 0
   private var start = TimeSource.Monotonic.markNow()
   private var testsFailed = emptyList<Pair<TestCase, TestResult>>()
   private var testsIgnored = 0
   private var testsPassed = 0
   private var specsFailed = emptyList<Descriptor.SpecDescriptor>()
   private var specsSeen = emptyList<Descriptor>()
   private var slow = 500.milliseconds
   private var verySlow = 5000.milliseconds

   private var formatter = FallbackDisplayNameFormatter.default(ProjectConfiguration())

   override suspend fun engineInitialized(context: EngineContext) {

      formatter = getFallbackDisplayNameFormatter(context.configuration.registry, context.configuration)

      println(">> Kotest")
      print("- Test plan has ")
      print(context.suite.specs.size.toString())
      println(" specs")
      println()
   }

   override suspend fun engineFinished(t: List<Throwable>) {

      if (specsSeen.isEmpty()) return

      if (t.isNotEmpty()) {
         errors += t.size
         t.forEach {
            printThrowable(it, 0)
         }
      }

      val duration = start.elapsedNow()
      val seconds = duration / 1000

      if (errors == 0) {
         println(">> All tests passed")
      } else {
         println(">> There were test failures")
         println()
         specsFailed.distinct().forEach { spec ->
            println(" ${formatter.format(spec.kclass)}")
            testsFailed.filter { it.first.spec::class.toDescriptor() == spec }.forEach { (testCase, _) ->
               println(" - ${formatter.formatTestPath(testCase, " -- ")}")
            }
         }
      }

      println()
      printSpecCounts()
      printTestsCounts()
      print("Time:    ")
      println("${seconds}s")
   }

   private fun printThrowable(error: Throwable?, padding: Int) {
      if (error != null) {
         val message = error.message
         if (message != null) {
            println(message.padStart(padding, ' '))
         }
//         error.stackTrace?.forEach {
//            println(red("".padStart(padding + 2, ' ') + it))
//         }
      }
   }

   private fun printSpecCounts() {
      val specsSeenSize = specsSeen.distinct().size
      val specsPassedSize = specsSeen.distinct().minus(specsFailed.toSet()).size
      val specsFailedSize = specsFailed.distinct().size
      print("Specs:   ")
      print("$specsPassedSize passed")
      print(", ")
      if (specsFailed.isEmpty()) {
         print("$specsFailedSize failed")
         print(", ")
      } else {
         print("$specsFailedSize failed")
         print(", ")
      }
      println("$specsSeenSize total")
   }

   private fun printTestsCounts() {
      print("Tests:   ")
      print("$testsPassed passed")
      print(", ")
      if (testsFailed.isEmpty()) {
         print("${testsFailed.size} failed")
         print(", ")
      } else {
         print("${testsFailed.size} failed")
         print(", ")
      }
      if (testsIgnored > 0) {
         print("$testsIgnored ignored")
         print(", ")
      } else {
         print("$testsIgnored ignored")
         print(", ")
      }
      println("${testsPassed + testsFailed.size + testsIgnored} total")
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      specsSeen = specsSeen + kclass.toDescriptor()
      val specCount = specsSeen.size
      print("$specCount. ".padEnd(4, ' '))
      println(formatter.format(kclass))
   }

   override suspend fun specFinished(kclass: KClass<*>, result: TestResult) {
      if (result.isErrorOrFailure) {
         errors++
         specsFailed = specsFailed + kclass.toDescriptor()
         printThrowable(result.errorOrNull, 4)
      }
      println()
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      testsIgnored++
      print("".padEnd(testCase.descriptor.depth() * 4, ' '))
      print("- " + formatter.format(testCase))
      println(" IGNORED")
   }

   private fun durationString(duration: Duration): String {
      return when {
         duration in slow..verySlow -> "(${duration.inWholeMilliseconds}ms)"
         duration > verySlow -> "(${duration.inWholeMilliseconds}ms)"
         else -> ""
      }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      // only leaf tests or failed containers contribute to the counts
      when (result) {
         is TestResult.Success -> if (testCase.type == TestType.Test) testsPassed++
         is TestResult.Failure, is TestResult.Error -> {
            errors++
            testsFailed = testsFailed + Pair(testCase, result)
            specsFailed = specsFailed + testCase.descriptor.spec()
         }

         else -> Unit
      }

      // we only print the name and status for leafs, as containers are printed in advance
      if (testCase.type == TestType.Test) {
         print("".padEnd(testCase.descriptor.depth() * 4, ' '))
         print("- " + formatter.format(testCase))
         when (result) {
            is TestResult.Success -> print(" OK")
            is TestResult.Error -> print(" ERROR")
            is TestResult.Failure -> print(" FAILED")
            is TestResult.Ignored -> print(" IGNORED")
         }

         if (result.duration > slow) {
            print(" ${durationString(result.duration)}")
         }
         println()
      }

      if (result.errorOrNull != null) {
         println()
         printThrowable(result.errorOrNull, testCase.descriptor.depth() * 4)
         println()
      }
   }

   override suspend fun testStarted(testCase: TestCase) {
      // containers we display straight away without pass / fail message
      if (testCase.type == TestType.Container) {
         print("".padEnd(testCase.descriptor.depth() * 4, ' '))
         println("+ " + formatter.format(testCase))
      }
   }
}
