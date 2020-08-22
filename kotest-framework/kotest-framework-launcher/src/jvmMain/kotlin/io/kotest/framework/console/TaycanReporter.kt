package io.kotest.framework.console

import com.github.ajalt.mordant.TermColors
import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import kotlin.reflect.KClass

/**
 * Generates test output in the 'augustus' Kotest style.
 */
class TaycanReporter(private val term: TermColors) : ConsoleWriter {

   private var start = System.currentTimeMillis()
   private var testsFailed = 0
   private var testsIgnored = 0
   private var testsPassed = 0
   private var specsFailed = emptyList<Description>()
   private var specsPassed = 0

   private fun green(str: String) = term.green(str)
   private fun greenBold(str: String) = term.green.plus(term.bold).invoke(str)
   private fun red(str: String) = term.red(str)
   private fun redBold(str: String) = term.red.plus(term.bold).invoke(str)
   private fun yellow(str: String) = term.yellow(str)
   private fun yellowBold(str: String) = term.yellow.plus(term.bold).invoke(str)
   private fun black(str: String) = term.black(str)
   private fun white(str: String) = term.white(str)
   private fun whiteBold(str: String) = term.white.plus(term.bold).invoke(str)
   private fun blackOnGreen(str: String) = term.black.on(term.green).invoke(str)
   private fun blackOnRed(str: String) = term.black.on(term.red).invoke(str)
   private fun blackOnBrightRed(str: String) = term.black.on(term.brightRed).invoke(str)
   private fun whiteOnGreen(str: String) = term.white.on(term.green).invoke(str)
   private fun whiteOnBrightRed(str: String) = term.white.on(term.brightRed).invoke(str)

   private val intros = listOf(
      "Stoking the Kotest engine with power crystals",
      "Engaging Kotest at warp factor 9",
      "Preparing to sacrifice your code to the gods of test",
      "Initializing all Kotest subsystems",
      "Battle commanders are ready to declare war on bugs",
      "Be afraid. Be very afraid - of failing tests",
      "Lock test-foils in attack position"
   )

   override fun engineStarted(classes: List<KClass<out Spec>>) {
      print(whiteBold(">> "))
      println(whiteBold(intros.shuffled().first()))
      println(whiteBold("${classes.size} specs will be executed"))
      println()
   }

   override fun hasErrors(): Boolean {
      return testsFailed > 0
   }

   override fun engineFinished(t: List<Throwable>) {
      val duration = System.currentTimeMillis() - start
      val seconds = duration / 1000

      if (testsFailed == 0) {
         println(whiteBold(">> All tests passed"))
      } else {
         println(redBold(">> There were test failures"))
         specsFailed.distinct().forEach {
            println(red("- ${it.displayName()}"))
         }
      }

      println()
      printSpecCounts()
      printTestsCounts()
      print(white("Time:    "))
      println(whiteBold("${seconds}s"))
   }

   private fun printSpecCounts() {
      print(white("Specs:   "))
      print(greenBold("$specsPassed passed"))
      print(white(", "))
      if (specsFailed.isEmpty()) {
         print(whiteBold("${specsFailed.size} failed"))
         print(whiteBold(", "))
      } else {
         print(redBold("${specsFailed.size} failed"))
         print(whiteBold(", "))
      }
      println(white("${testsPassed + testsFailed} total"))
   }

   private fun printTestsCounts() {
      print(white("Tests:   "))
      print(greenBold("$testsPassed passed"))
      print(white(", "))
      if (testsFailed > 0) {
         print(redBold("$testsFailed failed"))
         print(white(", "))
      } else {
         print(whiteBold("$testsFailed failed"))
         print(white(", "))
      }
      if (testsIgnored > 0) {
         print(yellowBold("$testsIgnored ignored"))
         print(white(", "))
      } else {
         print(whiteBold("$testsIgnored ignored"))
         print(white(", "))
      }
      println(white("${testsPassed + testsFailed} total"))
   }

   override fun specStarted(kclass: KClass<out Spec>) {
      println(white(kclass.toDescription().displayName()))
   }

   override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
      if (t != null) {
         specsFailed += kclass.toDescription()
      }
      println()
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      when (result.status) {
         TestStatus.Success -> testsPassed++
         TestStatus.Failure, TestStatus.Error -> {
            testsFailed++
            specsFailed += testCase.description.spec()
         }
         else -> testsIgnored++
      }
      when (result.status) {
         TestStatus.Success -> print(blackOnGreen("  OK   "))
         TestStatus.Error -> print(blackOnBrightRed(" ERROR "))
         TestStatus.Failure -> print(blackOnBrightRed(" ERROR "))
         else -> Unit
      }
      print("  ")
      println(testCase.displayName)
   }

   override fun testStarted(testCase: TestCase) {
   }
}
