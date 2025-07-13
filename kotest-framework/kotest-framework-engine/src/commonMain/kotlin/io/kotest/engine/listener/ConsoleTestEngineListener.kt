package io.kotest.engine.listener

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.console.consoleRenderer
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.engine.test.names.formatTestPath
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/**
 * A [TestEngineListener] that outputs to std out.
 *
 * It delegates to a [io.kotest.engine.console.ConsoleRenderer] to do the actual rendering on each platform,
 * to take advantage of enchanced console capabilities where available.
 */
open class ConsoleTestEngineListener : AbstractTestEngineListener() {

   private var errors = 0
   private var start = TimeSource.Monotonic.markNow()
   private var testsFailed = emptyList<Pair<TestCase, TestResult>>()
   private var testsIgnored = 0
   private var testsPassed = 0
   private var specsFailed = emptyList<Descriptor.SpecDescriptor>()
   private var specsSeen = emptyList<Descriptor>()
   private var slow = 500.milliseconds
   private var verySlow = 5000.milliseconds

   private var formatter = FallbackDisplayNameFormatter.Companion.default()

   private val intros = listOf(
      "Feeding the kotest engine with freshly harvested tests",
      "Engaging kotest engine at warp factor 9",
      "Harvesting the test fields",
      "Preparing to sacrifice your code to the gods of testing",
      "Hamsters are turning the wheels of kotest",
      "Battle commanders are ready to declare war on bugs",
      "Be afraid - be very afraid - of failing tests",
      "The point is, ladies and gentlemen, that green is good",
      "Lock test-foils in attack position",
      "Fasten your seatbelts. It's going to be a bumpy test-run",
      "Lets crack open this test suite",
      "Lets get testing, I'm on the clock here",
      "Test time is the best time",
      "Open the test suite doors, HAL",
      "Mama always said testing was like a box of chocolates. You don't know which ones are gonna fail",
      "A test suite. Shaken, not stirred",
      "I'm going to make him a test he can't refuse",
      "You testing me? I don't see any other tests here, so you must be testing me",
      "I love the smell of tests in the morning",
      "Do you feel lucky punk? Do you think your tests will pass? Well, do ya?",
      "Mirab, with tests unfurled",
      "Dolly works 9 to 5. I test 24/7",
      "A test suite's gotta do what a test suite's gotta do",
      "I test code and chew bubblegum, and I'm all out of bubblegum",
      "ChatGPT ain't got nothing on me when it comes to testing",
   )

   override suspend fun engineInitialized(context: EngineContext) {

      formatter = getFallbackDisplayNameFormatter(context.projectConfigResolver, context.testConfigResolver)

      consoleRenderer.println(consoleRenderer.bold(">> Kotest"))
      consoleRenderer.println("- " + intros.shuffled().first())
      consoleRenderer.println("- Test plan has " + consoleRenderer.greenBold(context.suite.specs.size.toString()) + " specs")
      consoleRenderer.println()
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

      if (errors == 0) {
         consoleRenderer.println(consoleRenderer.bold(">> All tests passed"))
      } else {
         consoleRenderer.println(consoleRenderer.redBold(">> There were test failures"))
         consoleRenderer.println()
         specsFailed.distinct().forEach { spec ->
            // todo fix this to use the proper class name
            consoleRenderer.println(consoleRenderer.brightRedBold(" ${formatter.format(this::class)}"))
            testsFailed.filter { it.first.spec::class.toDescriptor() == spec }.forEach { (testCase, _) ->
               consoleRenderer.println(consoleRenderer.brightRed(" - ${formatter.formatTestPath(testCase, " -- ")}"))
            }
         }
      }

      consoleRenderer.println()
      printSpecCounts()
      printTestsCounts()
      consoleRenderer.print("Time:    ")
      if (duration.inWholeSeconds > 60)
         consoleRenderer.println(consoleRenderer.bold("${duration.inWholeMinutes}m ${duration.div(60).inWholeSeconds}s"))
      else
         consoleRenderer.println(consoleRenderer.bold("${duration.inWholeSeconds}s"))
   }

   private fun printThrowable(error: Throwable?, padding: Int) {
      if (error != null) {
         val message = error.message
         if (message != null) {
            consoleRenderer.println(consoleRenderer.brightRed(message.padStart(padding, ' ')))
         }
         printlnStackTrace(error, padding)
      }
   }

   open fun printlnStackTrace(error: Throwable, padding: Int) {
   }

   private fun printSpecCounts() {
      val specsSeenSize = specsSeen.distinct().size
      val specsPassedSize = specsSeen.distinct().minus(specsFailed.toSet()).size
      val specsFailedSize = specsFailed.distinct().size
      consoleRenderer.print("Specs:   ")
      consoleRenderer.print(consoleRenderer.greenBold("$specsPassedSize passed"))
      consoleRenderer.print(", ")
      if (specsFailed.isEmpty()) {
         consoleRenderer.print(consoleRenderer.bold("$specsFailedSize failed"))
         consoleRenderer.print(consoleRenderer.bold(", "))
      } else {
         consoleRenderer.print(consoleRenderer.redBold("$specsFailedSize failed"))
         consoleRenderer.print(consoleRenderer.bold(", "))
      }
      consoleRenderer.println("$specsSeenSize total")
   }

   private fun printTestsCounts() {
      consoleRenderer.print("Tests:   ")
      consoleRenderer.print(consoleRenderer.greenBold("$testsPassed passed"))
      consoleRenderer.print(", ")
      if (testsFailed.isEmpty()) {
         consoleRenderer.print(consoleRenderer.bold("${testsFailed.size} failed"))
         consoleRenderer.print(", ")
      } else {
         consoleRenderer.print(consoleRenderer.redBold("${testsFailed.size} failed"))
         consoleRenderer.print(", ")
      }
      if (testsIgnored > 0) {
         consoleRenderer.print(consoleRenderer.yellowBold("$testsIgnored ignored"))
         consoleRenderer.print(", ")
      } else {
         consoleRenderer.print(consoleRenderer.bold("$testsIgnored ignored"))
         consoleRenderer.print(", ")
      }
      consoleRenderer.println("${testsPassed + testsFailed.size + testsIgnored} total")
   }

   override suspend fun specStarted(ref: SpecRef) {
      specsSeen = specsSeen + ref.kclass.toDescriptor()
      val specCount = specsSeen.size
      consoleRenderer.print(consoleRenderer.bold("$specCount. ".padEnd(4, ' ')))
      consoleRenderer.bold(formatter.format(ref.kclass))
   }

   override suspend fun specFinished(ref: SpecRef, result: TestResult) {
      if (result.isErrorOrFailure) {
         consoleRenderer.println("${ref.kclass} $result")
         errors++
         specsFailed = specsFailed + ref.kclass.toDescriptor()
         printThrowable(result.errorOrNull, 4)
      }
      consoleRenderer.println()
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      testsIgnored++
      consoleRenderer.print("".padEnd(testCase.descriptor.depth() * 4, ' '))
      consoleRenderer.print("- " + formatter.format(testCase))
      consoleRenderer.println(consoleRenderer.brightYellowBold(" IGNORED"))
   }

   private fun durationString(duration: Duration): String {
      return when {
         duration in slow..verySlow -> consoleRenderer.brightYellow("(${duration.inWholeMilliseconds}ms)")
         duration > verySlow -> consoleRenderer.brightRed("(${duration.inWholeMilliseconds}ms)")
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

      // we only consoleRenderer.print the name and status for leafs, as containers are consoleRenderer.printed in advance
      if (testCase.type == TestType.Test) {
         consoleRenderer.print("".padEnd(testCase.descriptor.depth() * 4, ' '))
         consoleRenderer.print("- " + formatter.format(testCase))
         when (result) {
            is TestResult.Success -> consoleRenderer.print(consoleRenderer.greenBold(" OK"))
            is TestResult.Error -> consoleRenderer.print(consoleRenderer.brightRed(" ERROR"))
            is TestResult.Failure -> consoleRenderer.print(consoleRenderer.brightRed(" FAILED"))
            is TestResult.Ignored -> consoleRenderer.print(consoleRenderer.brightYellow(" IGNORED"))
         }

         if (result.duration > slow) {
            consoleRenderer.print(" ${durationString(result.duration)}")
         }
         consoleRenderer.println()
      }

      if (result.errorOrNull != null) {
         consoleRenderer.println()
         printThrowable(result.errorOrNull, testCase.descriptor.depth() * 4)
         consoleRenderer.println()
      }
   }

   override suspend fun testStarted(testCase: TestCase) {
      // containers we display straight away without pass / fail message
      if (testCase.type == TestType.Container) {
         consoleRenderer.print("".padEnd(testCase.descriptor.depth() * 4, ' '))
         consoleRenderer.println("+ " + formatter.format(testCase))
      }
   }
}
