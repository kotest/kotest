package io.kotest.runner.console

import com.github.ajalt.mordant.TermColors
import io.kotest.Description
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.TestStatus
import io.kotest.TestType
import io.kotest.core.fromSpecClass
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

private val isWindows = System.getProperty("os.name").contains("win")

interface Symbol {
  fun print(term: TermColors): String
}

object SuccessSymbol : Symbol {
  override fun print(term: TermColors): String = term.brightGreen(if (isWindows) "√" else "✔")
}

object FailureSymbol : Symbol {
  override fun print(term: TermColors): String = term.brightRed(if (isWindows) "X" else "✘")
}

object IgnoredSymbol : Symbol {
  override fun print(term: TermColors): String = term.gray("-")
}

class MochaConsoleWriter(private val term: TermColors,
                         private val slow: Int = 1000,
                         private val verySlow: Int = 3000) : ConsoleWriter {

  private val margin = " ".repeat(2)

  private val tests = mutableListOf<TestCase>()
  private val results = mutableMapOf<Description, TestResult>()
  private var n = 0
  private var start = 0L
  private var errors = false

  private fun Description.indent(): String = "\t".repeat(parents.size)

  override fun hasErrors(): Boolean = errors

  @UseExperimental(ExperimentalTime::class)
  private fun testLine(testCase: TestCase, result: TestResult): String {
    val name = when (result.status) {
      TestStatus.Failure, TestStatus.Error -> term.brightRed(testCase.name + " *** FAILED ***")
      TestStatus.Success -> testCase.name
      TestStatus.Ignored -> term.gray(testCase.name + " ??? IGNORED ???")
    }
    val symbol = when (result.status) {
      TestStatus.Success -> SuccessSymbol
      TestStatus.Error, TestStatus.Failure -> FailureSymbol
      TestStatus.Ignored -> IgnoredSymbol
    }
    val duration = when (testCase.type) {
      TestType.Test -> durationString(result.duration.toLongMilliseconds())
      else -> ""
    }
    return "$margin${testCase.description.indent()} ${symbol.print(term)} $name $duration".padEnd(80, ' ')
  }

  private fun durationString(durationMs: Long): String {
    return when {
      durationMs in slow..verySlow -> term.brightYellow("(${durationMs}ms)")
      durationMs > verySlow -> term.brightRed("(${durationMs}ms)")
      else -> ""
    }
  }

  private fun cause(testCase: TestCase, message: String?): String {
    return term.brightRed("$margin\tcause: $message (${testCase.source.fileName}:${testCase.source.lineNumber})")
  }

  override fun afterSpecClass(klass: KClass<out Spec>, t: Throwable?) {

    n += 1
    val specDesc = Description.fromSpecClass(klass)

    if (t == null) {
      println("$margin$n) " + term.brightWhite(klass.qualifiedName!!))
    } else {
      errors = true
      print(margin)
      println(term.red(specDesc.name + " *** FAILED ***"))
      println(term.red("$margin\tcause: ${t.message})"))
    }
    println()

    tests.filter { it.spec::class.qualifiedName == klass.qualifiedName }.forEach {
      val result = results[it.description]
      if (result != null) {
        val line = testLine(it, result)
        println(line)
        when (result.status) {
          TestStatus.Error, TestStatus.Failure -> {
            errors = true
            println()
            println(cause(it, result.error?.message))
            println()
          }
          else -> {}
        }
      }
    }

    println()
  }

  override fun engineStarted(classes: List<KClass<out Spec>>) {
    start = System.currentTimeMillis()
  }

  override fun specInitialisationFailed(klass: KClass<out Spec>, t: Throwable) {

    n += 1
    val specDesc = Description.fromSpecClass(klass)
    errors = true

    print(margin)
    println(term.red(specDesc.name + " *** FAILED ***"))
    println(term.red("$margin\tcause: ${t.message})"))
    println()
  }

  override fun enterTestCase(testCase: TestCase) {
    tests.add(testCase)
  }

  override fun exitTestCase(testCase: TestCase, result: TestResult) {
    results[testCase.description] = result
  }

  private fun padNewLines(str: String, pad: String): String = str.lines().joinToString("\n") { "$pad$it" }

  @UseExperimental(ExperimentalTime::class)
  override fun engineFinished(t: Throwable?) {

    val duration = (System.currentTimeMillis() - start).milliseconds

    val ignored = results.filter { it.value.status == TestStatus.Ignored }
    val failed = results.filter { it.value.status == TestStatus.Failure || it.value.status == TestStatus.Error }
    val passed = results.filter { it.value.status == TestStatus.Success }

    val specs = tests.map { it.spec.description() }.distinct()
    val specDistinctCount = specs.distinct().size

    println()
    println(term.brightWhite("${margin}Kotest completed in ${duration.toLongMilliseconds()} seconds / ${duration.toLongMilliseconds()} milliseconds"))
    println("${margin}Executed $specDistinctCount specs containing ${failed.size + passed.size + ignored.size} tests")
    println("$margin${passed.size} passed, ${failed.size} failed, ${ignored.size} ignored")
    if (failed.isNotEmpty()) {
      println()
      println(term.brightWhite("$margin----------------------------- ${failed.size} FAILURES -----------------------------"))
      failed.forEach {
        println()
        println("$margin${term.brightRed(if (isWindows) "X" else "✘")} ${term.brightWhite(it.key.fullName())}")
        println()
        val error = it.value.error
        if (error != null) {
          val msg = error.message
          val stackTrace = error.stackTrace.joinToString("\n")
          if (msg != null) {
            println(term.brightRed(padNewLines(msg, margin)))
            println()
          }
          println(margin + term.red(error.javaClass.name))
          println(term.red(padNewLines(stackTrace, margin.repeat(2))))
        }
      }
    }
  }
}
