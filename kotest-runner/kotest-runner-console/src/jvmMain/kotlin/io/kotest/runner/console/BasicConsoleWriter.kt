package io.kotest.runner.console

import com.github.ajalt.mordant.TermColors
import io.kotest.core.spec.Spec
import io.kotest.core.spec.description
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import java.time.Duration
import kotlin.reflect.KClass

/**
 * Writes to the console.
 */
class BasicConsoleWriter : ConsoleWriter {

   private var errors = false
   private val term = TermColors(TermColors.Level.ANSI256)

   private fun Description.indent(): String = "\t".repeat(parents.size)
   private fun Description.indented(): String = "${indent()}$name"

   private var start = 0L
   private var n = 0

   private val tests = mutableListOf<TestCase>()
   private val testResults = mutableMapOf<Description, TestResult>()

   private fun green(str: String) = println(term.green(str))
   private fun red(str: String) = println(term.red(str))
   private fun yellow(str: String) = println(term.yellow(str))

   override fun hasErrors(): Boolean = errors

   override fun engineStarted(classes: List<KClass<out Spec>>) {
      start = System.currentTimeMillis()
   }

   override fun testStarted(testCase: TestCase) {
      tests.add(testCase)
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      testResults[testCase.description] = result
   }

   override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {

      n += 1
      val specDesc = kclass.description()

      if (t == null) {
         print("$n) ")
         term.green(specDesc.name.displayName())
      } else {
         errors = true
         red(specDesc.name.displayName() + " *** FAILED ***")
         red("  \tcause: ${t.message})")
      }

      tests
         .filter { it.spec::class.qualifiedName == kclass.qualifiedName }
         .forEach { testCase ->
            val result = testResults[testCase.description]
            when (result?.status) {
               null -> red("${testCase.description.name.displayName()} did not complete")
               TestStatus.Success -> green("   " + testCase.description.indented())
               TestStatus.Error, TestStatus.Failure -> {
                  errors = true
                  red("   " + testCase.description.indented() + " *** FAILED ***")
                  result.error?.message?.apply {
                     red(testCase.description.indent() + "  \tcause: $this (${testCase.source.fileName}:${testCase.source.lineNumber})")
                  }
               }
               TestStatus.Ignored -> yellow("   " + testCase.description.indented() + " (Ignored)")
            }
         }
   }

   override fun engineFinished(t: List<Throwable>) {

      val duration = Duration.ofMillis(System.currentTimeMillis() - start)

      val ignored = testResults.filter { it.value.status == TestStatus.Ignored }
      val failed = testResults.filter { it.value.status == TestStatus.Failure || it.value.status == TestStatus.Error }
      val passed = testResults.filter { it.value.status == TestStatus.Success }

      val specs = tests.map { it.spec::class.description() }.distinct()
      val specDistinctCount = specs.distinct().size

      println()
      println("Kotest completed in ${duration.seconds} seconds, ${duration.toMillis()} millis")
      println("Specs: completed $specDistinctCount, tests ${failed.size + passed.size + ignored.size}")
      println("Tests: passed ${passed.size}, failed ${failed.size}, ignored ${ignored.size}")
      if (failed.isNotEmpty()) {
         red("*** ${failed.size} TESTS FAILED ***")
         println("Specs with failing tests:")
         failed.map { it.key.spec() }
            .distinct()
            .sortedBy { it.name.displayName() }
            .forEach {
               red(" - ${it.name.displayName()}")
            }
      }
   }
}
