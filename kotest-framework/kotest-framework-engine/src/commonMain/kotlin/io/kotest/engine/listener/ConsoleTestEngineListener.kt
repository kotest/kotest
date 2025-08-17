package io.kotest.engine.listener

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.console.consoleRenderer
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.names.DisplayNameFormatting
import kotlin.reflect.KClass
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
   private var specsFailed = emptyList<KClass<*>>()
   private var specsSeen = emptyList<Descriptor>()
   private var slow = 500.milliseconds
   private var verySlow = 5000.milliseconds

   private var formatter = DisplayNameFormatting(null)

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
      "I feel the need - the need for tests",
      "I'm going to make him a test he can't refuse",
      "Here's looking at you, tests",
      "You testing me? I don't see any other tests here, so you must be testing me",
      "I love the smell of tests in the morning",
      "Do you feel lucky punk? Do you think your tests will pass? Well, do ya?",
      "Mirab, with tests unfurled",
      "Dolly works 9 to 5. I test 24/7",
      "A test suite's gotta do what a test suite's gotta do",
      "I test code and chew bubblegum, and I'm all out of bubblegum",
      "ChatGPT ain't got nothing on me when it comes to testing",
      "Frankly, my dear, I don't give a test",
      "Show me the tests!",
      "Nobody puts Kotest in a corner",
      "The first rule of Kotest is: you do not talk about tests",
      "I see dead tests",
      "You had me at tests",
      "Houston, we have a test suite",
      "To infinity and beyond! (with tests)",
      "May the tests be with you",
      "I'll be back... with more tests",
      "Hasta la vista, tests",
      "You can't handle the tests!",
      "Life is like a test suite, you never know what you're gonna get",
      "Keep calm and test on",
      "In the test suite of life, there are no failures, only learning opportunities",
      "Test like nobody's watching",
      "Test hard, test often",
   )

   override suspend fun engineInitialized(context: EngineContext) {

      formatter = DisplayNameFormatting(context.projectConfig)

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
            consoleRenderer.println(consoleRenderer.brightRedBold(" ${formatter.format(spec)}"))
            testsFailed.filter { it.first.spec::class == spec }.forEach { (testCase, _) ->
               consoleRenderer.println(consoleRenderer.brightRed(" - ${formatter.formatTestPath(testCase, " -- ")}"))
            }
         }
      }

      consoleRenderer.println()
      printSpecCounts()
      printTestsCounts()

      val str = buildString {
         append("Time:    ")
         if (duration.inWholeSeconds > 60)
            append(consoleRenderer.bold("${duration.inWholeMinutes}m ${duration.div(60).inWholeSeconds}s"))
         else
            append(consoleRenderer.bold("${duration.inWholeSeconds}s"))
      }
      consoleRenderer.println(str)
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
      val str = buildString {
         append("Specs:   ${consoleRenderer.greenBold("$specsPassedSize passed")}, ")
         if (specsFailed.isEmpty()) {
            append(consoleRenderer.bold("$specsFailedSize failed") + ", ")
         } else {
            append(consoleRenderer.redBold("$specsFailedSize failed") + ", ")
         }
         append("$specsSeenSize total")
      }
      consoleRenderer.println(str)
   }

   private fun printTestsCounts() {
      val str = buildString {
         append("Tests:   ${consoleRenderer.greenBold("$testsPassed passed")}, ")
         if (testsFailed.isEmpty()) {
            append(consoleRenderer.bold("${testsFailed.size} failed") + ", ")
         } else {
            append(consoleRenderer.redBold("${testsFailed.size} failed") + ", ")
         }
         if (testsIgnored > 0) {
            append(consoleRenderer.yellowBold("$testsIgnored ignored") + ", ")
         } else {
            append(consoleRenderer.bold("$testsIgnored ignored") + ", ")
         }
         append("${testsPassed + testsFailed.size + testsIgnored} total")
      }
      consoleRenderer.println(str)
   }

   override suspend fun specStarted(ref: SpecRef) {
      specsSeen = specsSeen + ref.kclass.toDescriptor()
      val specCount = specsSeen.size
      val str = buildString {
         append(consoleRenderer.bold("$specCount. ".padEnd(4, ' ')))
         append(consoleRenderer.brightYellowBold(" IGNORED"))
      }
      consoleRenderer.println(str)
   }

   override suspend fun specFinished(ref: SpecRef, result: TestResult) {
      if (result.isErrorOrFailure) {
         consoleRenderer.println("${ref.kclass} $result")
         errors++
         specsFailed = specsFailed + ref.kclass
         printThrowable(result.errorOrNull, 4)
      }
      consoleRenderer.println()
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      testsIgnored++
      val str = buildString {
         append("".padEnd(testCase.descriptor.depth() * 4, ' '))
         append("- ")
         append(formatter.format(testCase) + consoleRenderer.brightYellowBold(" IGNORED"))
      }
      consoleRenderer.println(str)
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
            specsFailed = specsFailed + testCase.spec::class
         }

         else -> Unit
      }

      // we only consoleRenderer.print the name and status for leafs, as containers are consoleRenderer.printed in advance
      if (testCase.type == TestType.Test) {
         val r = when (result) {
            is TestResult.Success -> consoleRenderer.greenBold(" OK")
            is TestResult.Error -> consoleRenderer.brightRed(" ERROR")
            is TestResult.Failure -> consoleRenderer.brightRed(" FAILED")
            is TestResult.Ignored -> consoleRenderer.brightYellow(" IGNORED")
         }
         val str = buildString {
            append("".padEnd(testCase.descriptor.depth() * 4, ' ') + "- " + formatter.format(testCase) + r)
            if (result.duration > slow) {
               append(" ${durationString(result.duration)}")
            }
         }
         consoleRenderer.println(str)
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
         val str = buildString {
            append("".padEnd(testCase.descriptor.depth() * 4, ' '))
            append("+ ")
            append(formatter.format(testCase))
         }
         consoleRenderer.println(str)
      }
   }
}
