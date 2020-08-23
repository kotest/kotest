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

   private val bullet = 0x2022.toChar().toString()
   private var term: TermColors = TermColors()

   override fun setTerm(term: TermColors) {
      this.term = term
   }

   private var start = System.currentTimeMillis()
   private var testsFailed = emptyList<Pair<TestCase, TestResult>>()
   private var testsIgnored = 0
   private var testsPassed = 0
   private var specsFailed = emptyList<Description>()
   private var specsPassed = 0
   private var specCount = 0

   private fun green(str: String) = term.green(str)
   private fun greenBold(str: String) = term.green.plus(term.bold).invoke(str)
   private fun red(str: String) = term.red(str)
   private fun brightRed(str: String) = term.brightRed(str)
   private fun brightRedBold(str: String) = term.brightRed.plus(term.bold).invoke(str)
   private fun redBold(str: String) = term.red.plus(term.bold).invoke(str)
   private fun yellow(str: String) = term.yellow(str)
   private fun brightYellow(str: String) = term.brightYellow(str)
   private fun yellowBold(str: String) = term.yellow.plus(term.bold).invoke(str)
   private fun black(str: String) = term.black(str)
   private fun white(str: String) = term.white(str)
   private fun bold(str: String) = term.bold(str)
   private fun blackOnGreen(str: String) = term.black.on(term.green).invoke(str)
   private fun blackOnRed(str: String) = term.black.on(term.red).invoke(str)
   private fun blackOnBrightRed(str: String) = term.black.on(term.brightRed).invoke(str)
   private fun whiteOnGreen(str: String) = term.white.on(term.green).invoke(str)
   private fun whiteOnBrightRed(str: String) = term.white.on(term.brightRed).invoke(str)

   private val intros = listOf(
      "Powering the Kotest engine with freshly harvested tests",
      "Engaging Kotest at warp factor 9",
      "Preparing to sacrifice your code to the gods of test",
      "Initializing all Kotest subsystems",
      "Battle commanders are ready to declare war on bugs",
      "Be afraid - be very afraid - of failing tests",
      "The point is, ladies and gentlemen, that green is good",
      "Lock test-foils in attack position",
      "Lets crack open this test suite",
      "Lets get testing, I'm on the clock here",
      "Mirab, with tests unfurled",
      "A test suite's gotta do what a test suite's gotta do",
      "I test code and chew bubblegum, and I'm all out of bubblegum"
   )

   override fun engineStarted(classes: List<KClass<out Spec>>) {
      print(bold(">> "))
      println(bold(intros.shuffled().first()))
      print("Test plan has ")
      print(greenBold(classes.size.toString()))
      println(white(" specs"))
      println()
   }

   override fun hasErrors(): Boolean = testsFailed.isNotEmpty()

   override fun engineFinished(t: List<Throwable>) {
      val duration = System.currentTimeMillis() - start
      val seconds = duration / 1000

      if (testsFailed.isEmpty()) {
         println(bold(">> All tests passed"))
      } else {
         println(redBold(">> There were test failures"))
         println()
         specsFailed.distinct().forEach { spec ->
            println(brightRedBold(" ${spec.displayName()}"))
            testsFailed.filter { it.first.description.spec() == spec }.forEach { (testCase, result) ->
               println(brightRed("  - ${testCase.description.testDisplayPath().value}"))
            }
         }
      }

      println()
      printSpecCounts()
      printTestsCounts()
      print(white("Time:    "))
      println(bold("${seconds}s"))
   }

   private fun printThrowable(error: Throwable?, padding: Int) {
      if (error != null) {
         val stack = error.toString()
            .lines()
            .joinToString(System.lineSeparator()) { "".padStart(padding, ' ') + it }
         println(brightRed(stack))
      }
   }

   private fun printSpecCounts() {
      print("Specs:   ")
      print(greenBold("$specsPassed passed"))
      print(", ")
      if (specsFailed.isEmpty()) {
         print(bold("${specsFailed.size} failed"))
         print(bold(", "))
      } else {
         print(redBold("${specsFailed.size} failed"))
         print(bold(", "))
      }
      println("${specsFailed.size + specsPassed} total")
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
      specCount++
      print(bold("$specCount. ".padEnd(4, ' ')))
      println(bold(kclass.toDescription().displayName()))
   }

   override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
      if (t != null) {
         specsFailed += kclass.toDescription()
         printThrowable(t, 4)
      }
      println()
   }

   override fun testIgnored(testCase: TestCase) {
      testsIgnored++
      print("".padEnd(testCase.description.depth() * 4, ' '))
      print(bullet + " " + testCase.displayName)
      println(brightYellow(" IGNORED"))
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      // only leaf tests or failed containers contribute to the counts
      when (result.status) {
         TestStatus.Success -> if (testCase.type == TestType.Test) testsPassed++
         TestStatus.Failure, TestStatus.Error -> {
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
            TestStatus.Success -> println(greenBold(" OK"))
            TestStatus.Error -> println(brightRed(" ERROR"))
            TestStatus.Failure -> println(brightRed(" FAILED"))
            TestStatus.Ignored -> println(brightYellow(" IGNORED"))
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
