package io.kotest.engine.listener

import com.github.ajalt.mordant.TermColors
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.engine.test.names.formatTestPath
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/**
 * A [TestEngineListener] that outputs in a more colourful rich way.
 */
class EnhancedConsoleTestEngineListener(private val term: TermColors) : AbstractTestEngineListener() {

   private var errors = 0
   private var start = TimeSource.Monotonic.markNow()
   private var testsFailed = emptyList<Pair<TestCase, TestResult>>()
   private var testsIgnored = 0
   private var testsPassed = 0
   private var specsFailed = emptyList<Descriptor.SpecDescriptor>()
   private var specsSeen = emptyList<Descriptor>()
   private var slow = 500.milliseconds
   private var verySlow = 5000.milliseconds

   private var formatter = FallbackDisplayNameFormatter.default()

   private fun green(str: String) = term.green(str)
   private fun greenBold(str: String) = term.green.plus(term.bold).invoke(str)
   private fun red(str: String) = term.red(str)
   private fun brightRed(str: String) = term.brightRed(str)
   private fun brightRedBold(str: String) = term.brightRed.plus(term.bold).invoke(str)
   private fun redBold(str: String) = term.red.plus(term.bold).invoke(str)
   private fun yellow(str: String) = term.yellow(str)
   private fun brightYellow(str: String) = term.brightYellow(str)
   private fun brightYellowBold(str: String) = term.brightYellow.plus(term.bold).invoke(str)
   private fun yellowBold(str: String) = term.yellow.plus(term.bold).invoke(str)
   private fun bold(str: String) = term.bold(str)

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
      "I test code and chew bubblegum, and I'm all out of bubblegum"
   )

   override suspend fun engineInitialized(context: EngineContext) {

      formatter = getFallbackDisplayNameFormatter(context.projectConfigResolver, context.testConfigResolver)

      println(bold(">> Kotest"))
      println("- " + intros.shuffled().first())
      print("- Test plan has ")
      print(greenBold(context.suite.specs.size.toString()))
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

      if (errors == 0) {
         println(bold(">> All tests passed"))
      } else {
         println(redBold(">> There were test failures"))
         println()
         specsFailed.distinct().forEach { spec ->
            println(brightRedBold(" ${formatter.format(this::class)}"))
            testsFailed.filter { it.first.spec::class.toDescriptor() == spec }.forEach { (testCase, _) ->
               println(brightRed(" - ${formatter.formatTestPath(testCase, " -- ")}"))
            }
         }
      }

      println()
      printSpecCounts()
      printTestsCounts()
      print("Time:    ")
      if (duration.inWholeSeconds > 60)
         println(bold("${duration.inWholeMinutes}m ${duration.div(60).inWholeSeconds}s"))
      else
         println(bold("${duration.inWholeSeconds}s"))
   }

   private fun printThrowable(error: Throwable?, padding: Int) {
      if (error != null) {
         val message = error.message
         if (message != null) {
            println(brightRed(message.padStart(padding, ' ')))
         }
         error.stackTrace?.forEach {
            println(red("".padStart(padding + 2, ' ') + it))
         }
      }
   }

   private fun printSpecCounts() {
      val specsSeenSize = specsSeen.distinct().size
      val specsPassedSize = specsSeen.distinct().minus(specsFailed.toSet()).size
      val specsFailedSize = specsFailed.distinct().size
      print("Specs:   ")
      print(greenBold("$specsPassedSize passed"))
      print(", ")
      if (specsFailed.isEmpty()) {
         print(bold("$specsFailedSize failed"))
         print(bold(", "))
      } else {
         print(redBold("$specsFailedSize failed"))
         print(bold(", "))
      }
      println("$specsSeenSize total")
   }

   private fun printTestsCounts() {
      print("Tests:   ")
      print(greenBold("$testsPassed passed"))
      print(", ")
      if (testsFailed.isEmpty()) {
         print(bold("${testsFailed.size} failed"))
         print(", ")
      } else {
         print(redBold("${testsFailed.size} failed"))
         print(", ")
      }
      if (testsIgnored > 0) {
         print(yellowBold("$testsIgnored ignored"))
         print(", ")
      } else {
         print(bold("$testsIgnored ignored"))
         print(", ")
      }
      println("${testsPassed + testsFailed.size + testsIgnored} total")
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      specsSeen = specsSeen + kclass.toDescriptor()
      val specCount = specsSeen.size
      print(bold("$specCount. ".padEnd(4, ' ')))
      println(bold(formatter.format(kclass)))
   }

   override suspend fun specFinished(kclass: KClass<*>, result: TestResult) {
      if (result.isErrorOrFailure) {
         println("$kclass $result")
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
      println(brightYellowBold(" IGNORED"))
   }

   private fun durationString(duration: Duration): String {
      return when {
         duration in slow..verySlow -> term.brightYellow("(${duration.inWholeMilliseconds}ms)")
         duration > verySlow -> term.brightRed("(${duration.inWholeMilliseconds}ms)")
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
            is TestResult.Success -> print(greenBold(" OK"))
            is TestResult.Error -> print(brightRed(" ERROR"))
            is TestResult.Failure -> print(brightRed(" FAILED"))
            is TestResult.Ignored -> print(brightYellow(" IGNORED"))
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
