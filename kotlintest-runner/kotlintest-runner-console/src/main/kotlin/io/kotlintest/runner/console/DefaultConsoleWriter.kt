package io.kotlintest.runner.console

import com.andreapivetta.kolor.Color
import com.andreapivetta.kolor.Kolor
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

  private fun Description.indent(): String = "\t".repeat(parents.size)
  private fun Description.indented(): String = "${indent()}$name"
  private fun TestCase.indented(): String = description.indented()

  private val failed = mutableListOf<TestCase>()
  private val passed = mutableListOf<TestCase>()
  private val ignored = mutableListOf<TestCase>()
  private val specs = mutableListOf<KClass<out Spec>>()
  private var start = 0L

  private val tests = mutableListOf<TestCase>()
  private val results = mutableMapOf<Description, TestResult>()

  private fun green(str: String) = println(Kolor.foreground(str, Color.GREEN))
  private fun red(str: String) = println(Kolor.foreground(str, Color.RED))
  private fun yellow(str: String) = println(Kolor.foreground(str, Color.YELLOW))

  override fun engineStarted(classes: List<KClass<out Spec>>) {
    start = System.currentTimeMillis()
  }

  override fun beforeSpecClass(klass: KClass<out Spec>) {
    specs.add(klass)
  }

  //

  override fun enterTestCase(testCase: TestCase) {
    tests.add(testCase)
  }

  override fun exitTestCase(testCase: TestCase, result: TestResult) {
    results[testCase.description] = result
  }

  override fun afterSpecClass(klass: KClass<out Spec>, t: Throwable?) {

    val specDesc = Description.spec(klass)

    if (t == null) {
      green(specDesc.name)
    } else {
      red(specDesc.name + " *** FAILED ***")
      red("\tcause: ${t.message})")
    }

    tests.forEach {
      val result = results[it.description]
      when (result?.status) {
        null -> red("${it.description} did not complete")
        TestStatus.Success -> {
          green(it.indented())
          passed.add(it)
        }
        TestStatus.Error, TestStatus.Failure -> {
          red(it.indented() + " *** FAILED ***")
          result.error?.message?.apply {
            red(it.description.indent() + "\tcause: $this (${it.source.fileName}:${it.source.lineNumber})")
          }
          failed.add(it)
        }
        TestStatus.Ignored -> {
          yellow(it.indented() + " (Ignored)")
          ignored.add(it)
        }
      }
    }
  }

  override fun engineFinished(t: Throwable?) {
    val duration = Duration.ofMillis(System.currentTimeMillis() - start)
    val specDistinctCount = specs.distinct().size
    val specPluralOrSingular = if (specDistinctCount == 1) "spec" else "specs"
    println()
    println("KotlinTest completed in ${duration.seconds} seconds, ${duration.toMillis()} millis")
    println("$specDistinctCount $specPluralOrSingular containing ${failed.size + passed.size + ignored.size} tests")
    println("Tests: passed ${passed.size}, failed ${failed.size}, ignored ${ignored.size}")
    if (failed.isNotEmpty()) {
      red("*** ${failed.size} TESTS FAILED ***")
      println("Specs with failing tests:")
      failed.map { it.description.spec() }.distinct().sortedBy { it.name }.forEach {
        red(" - ${it.name}")
      }
    }
  }
}