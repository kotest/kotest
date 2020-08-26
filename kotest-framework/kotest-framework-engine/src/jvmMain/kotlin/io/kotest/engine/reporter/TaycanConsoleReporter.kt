package io.kotest.engine.reporter

import com.github.ajalt.mordant.TermColors
import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType
import kotlin.reflect.KClass

/**
 * Generates test output in the 'augustus' Kotest style.
 */
class TaycanConsoleReporter : ConsoleReporter {

   private var term: TermColors = TermColors()

   override fun setTerm(term: TermColors) {
      this.term = term
   }

   private var errors = 0
   private var start = System.currentTimeMillis()
   private var testsFailed = emptyList<Pair<TestCase, TestResult>>()
   private var testsIgnored = 0
   private var testsPassed = 0
   private var specsFailed = emptyList<Description>()
   private var specsSeen = emptyList<Description>()
   private var slow = 500
   private var verySlow = 5000

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
      "Preparing to sacrifice your code to the gods of test",
      "Hamsters are turning the wheels of kotest",
      "Battle commanders are ready to declare war on bugs",
      "Be afraid - be very afraid - of failing tests",
      "The point is, ladies and gentlemen, that green is good",
      "Lock test-foils in attack position",
      "Lets crack open this test suite",
      "Lets get testing, I'm on the clock here",
      "Mirab, with tests unfurled",
      "Dolly works 9 to 5. I test 24/7",
      "A test suite's gotta do what a test suite's gotta do",
      "I test code and chew bubblegum, and I'm all out of bubblegum"
   )

   override fun engineStarted(classes: List<KClass<out Spec>>) {
      println(bold(">> Kotest"))
      println("- " + intros.shuffled().first())
      print("- Test plan has ")
      print(greenBold(classes.size.toString()))
      println(" specs")
      println()
   }

   override fun hasErrors(): Boolean = errors > 0

   override fun engineFinished(t: List<Throwable>) {

      if (t.isNotEmpty()) {
         errors += t.size
         t.forEach {
            printThrowable(it, 0)
         }
      }

      val duration = System.currentTimeMillis() - start
      val seconds = duration / 1000

      if (errors == 0) {
         println(bold(">> All tests passed"))
      } else {
         println(redBold(">> There were test failures"))
         println()
         specsFailed.distinct().forEach { spec ->
            println(brightRedBold(" ${spec.displayName()}"))
            testsFailed.filter { it.first.description.spec() == spec }.forEach { (testCase, _) ->
               println(brightRed(" - ${testCase.description.testDisplayPath().value}"))
            }
         }
      }

      println()
      printSpecCounts()
      printTestsCounts()
      print("Time:    ")
      println(bold("${seconds}s"))
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
      val specsPassedSize = specsSeen.distinct().minus(specsFailed).size
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

   override fun specStarted(kclass: KClass<out Spec>) {
      specsSeen += kclass.toDescription()
      val specCount = specsSeen.size
      print(bold("$specCount. ".padEnd(4, ' ')))
      println(bold(kclass.toDescription().displayName()))
   }

   override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
      if (t != null) {
         errors++
         specsFailed += kclass.toDescription()
         printThrowable(t, 4)
      }
      println()
   }

   override fun testIgnored(testCase: TestCase) {
      testsIgnored++
      print("".padEnd(testCase.description.depth() * 4, ' '))
      print("- " + testCase.displayName)
      println(brightYellowBold(" IGNORED"))
   }

   private fun durationString(durationMs: Long): String {
      return when {
         durationMs in slow..verySlow -> term.brightYellow("(${durationMs}ms)")
         durationMs > verySlow -> term.brightRed("(${durationMs}ms)")
         else -> ""
      }
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      // only leaf tests or failed containers contribute to the counts
      when (result.status) {
         TestStatus.Success -> if (testCase.type == TestType.Test) testsPassed++
         TestStatus.Failure, TestStatus.Error -> {
            errors++
            testsFailed += Pair(testCase, result)
            specsFailed += testCase.description.spec()
         }
         else -> Unit
      }

      // we only print the name for leafs, as containers are printed in advance
      if (testCase.type == TestType.Test) {
         print("".padEnd(testCase.description.depth() * 4, ' '))
         print("- " + testCase.displayName)
         when (result.status) {
            TestStatus.Success -> print(greenBold(" OK"))
            TestStatus.Error -> print(brightRed(" ERROR"))
            TestStatus.Failure -> print(brightRed(" FAILED"))
            TestStatus.Ignored -> print(brightYellow(" IGNORED"))
         }

         if (result.duration > slow) {
            println(" ${durationString(result.duration)}")
         }
      }

      if (result.error != null) {
         println()
         printThrowable(result.error, testCase.description.depth() * 4)
         println()
      }
   }

   override fun testStarted(testCase: TestCase) {
      // containers we display straight away without pass / fail message
      if (testCase.type == TestType.Container) {
         print("".padEnd(testCase.description.depth() * 4, ' '))
         println("+ " + testCase.displayName)
      }
   }
}
