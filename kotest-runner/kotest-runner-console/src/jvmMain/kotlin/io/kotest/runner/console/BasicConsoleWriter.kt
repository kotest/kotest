package io.kotest.runner.console

import com.github.ajalt.mordant.TermColors
import io.kotest.Description
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.TestStatus
import io.kotest.core.fromSpecClass
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
  private val results = mutableMapOf<Description, TestResult>()

  private fun green(str: String) = println(term.green(str))
  private fun red(str: String) = println(term.red(str))
  private fun yellow(str: String) = println(term.yellow(str))

  override fun hasErrors(): Boolean = errors

  override fun engineStarted(classes: List<KClass<out Spec>>) {
    start = System.currentTimeMillis()
  }

  override fun enterTestCase(testCase: TestCase) {
    tests.add(testCase)
  }

  override fun exitTestCase(testCase: TestCase, result: TestResult) {
    results[testCase.description] = result
  }

  override fun afterSpecClass(klass: KClass<out Spec>, t: Throwable?) {

    n += 1
    val specDesc = Description.fromSpecClass(klass)

    if (t == null) {
      print("$n) ")
      term.green(specDesc.name)
    } else {
      errors = true
      red(specDesc.name + " *** FAILED ***")
      red("  \tcause: ${t.message})")
    }

    tests.filter { it.spec::class.qualifiedName == klass.qualifiedName }.forEach {
      val result = results[it.description]
      when (result?.status) {
        null -> red("${it.description} did not complete")
        TestStatus.Success -> green("   " + it.description.indented())
        TestStatus.Error, TestStatus.Failure -> {
          errors = true
          red("   " + it.description.indented() + " *** FAILED ***")
          result.error?.message?.apply {
            red(it.description.indent() + "  \tcause: $this (${it.source.fileName}:${it.source.lineNumber})")
          }
        }
        TestStatus.Ignored -> yellow("   " + it.description.indented() + " (Ignored)")
      }
    }
  }

  override fun engineFinished(t: Throwable?) {

    val duration = Duration.ofMillis(System.currentTimeMillis() - start)

    val ignored = results.filter { it.value.status == TestStatus.Ignored }
    val failed = results.filter { it.value.status == TestStatus.Failure || it.value.status == TestStatus.Error }
    val passed = results.filter { it.value.status == TestStatus.Success }

    val specs = tests.map { it.spec.description() }.distinct()
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
          .sortedBy { it.name }
          .forEach {
            red(" - ${it.name}")
          }
    }
  }
}
