package io.kotlintest.runner.console

import com.github.ajalt.mordant.TermColors
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.runner.jvm.TestEngineListener
import java.time.Duration
import kotlin.reflect.KClass

/**
 * Writes to the console.
 */
class DefaultConsoleWriter : TestEngineListener {

  private val term = TermColors()

  private fun Description.indent(): String = "\t".repeat(parents.size)
  private fun Description.indented(): String = "${indent()}$name"
  private fun TestCase.indented(): String = description.indented()

  private val failed = mutableListOf<TestCase>()
  private val passed = mutableListOf<TestCase>()
  private val ignored = mutableListOf<TestCase>()
  private val specs = mutableListOf<KClass<out Spec>>()
  private var start = 0L

  override fun beforeSpecClass(klass: KClass<out Spec>) {
    println(term.green(Description.spec(klass).name))
  }

  override fun exitTestCase(testCase: TestCase, result: TestResult) {
    specs.add(testCase.spec::class)
    when (result.status) {
      TestStatus.Success -> {
        println(term.green(testCase.indented()))
        passed.add(testCase)
      }
      TestStatus.Error, TestStatus.Failure -> {
        println(term.red(testCase.indented()) + " *** FAILED ***")
        result.error?.message?.apply {
          val assertion = testCase.description.indent() + "\tcause: " + term.red("$this (${testCase.line})")
          println(assertion)
        }
        failed.add(testCase)
      }
      TestStatus.Ignored -> {
        println(term.yellow(testCase.indented()))
        ignored.add(testCase)
      }
    }
  }

  override fun engineStarted(classes: List<KClass<out Spec>>) {
    start = System.currentTimeMillis()
  }

  override fun engineFinished(t: Throwable?) {
    val duration = Duration.ofMillis(System.currentTimeMillis() - start)
    val specDistinctCount = specs.distinct().size
    val specPluralOrSingular = if (specDistinctCount == 1) "spec" else "specs"
    println()
    println(term.blue("KotlinTest completed in ${duration.seconds} seconds, ${duration.toMillis()} millis"))
    println(term.blue("$specDistinctCount $specPluralOrSingular containing ${failed.size + passed.size + ignored.size} tests"))
    println(term.blue("Tests: passed ${passed.size}, failed ${failed.size}, ignored ${ignored.size}"))
    if (failed.isNotEmpty()) {
      println(term.red("*** ${failed.size} TESTS FAILED ***"))
      println("Specs with failing tests:")
      failed.map { it.description.spec() }.distinct().sortedBy { it.name }.forEach {
        println(" - ${it.name}")
      }
    }
  }
}