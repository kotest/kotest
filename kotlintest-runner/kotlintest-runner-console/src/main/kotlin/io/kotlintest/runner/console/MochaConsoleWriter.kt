package io.kotlintest.runner.console

import com.github.ajalt.mordant.TermColors
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.TestType
import java.time.Duration
import kotlin.reflect.KClass

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

class MochaConsoleWriter(private val slow: Int = 1000,
                         private val verySlow: Int = 3000) : ConsoleWriter {

  private val term = TermColors(TermColors.Level.ANSI256)

  private val tests = mutableListOf<TestCase>()
  private val results = mutableMapOf<Description, TestResult>()
  private var n = 0
  private var start = 0L
  private var errors = false

  private fun Description.indent(): String = "\t".repeat(parents.size)

  override fun hasErrors(): Boolean = errors

  private fun testLine(testCase: TestCase, result: TestResult): String {
    val name = when (result.status) {
      TestStatus.Failure, TestStatus.Error -> term.brightRed(testCase.name + " *** FAILED ***")
      TestStatus.Success -> testCase.name
      TestStatus.Ignored -> term.gray(testCase.name)
    }
    val symbol = when (result.status) {
      TestStatus.Success -> SuccessSymbol
      TestStatus.Error, TestStatus.Failure -> FailureSymbol
      TestStatus.Ignored -> IgnoredSymbol
    }
    val duration = when (testCase.type) {
      TestType.Test -> durationMillis(result.duration)
      else -> ""
    }
    return "${testCase.description.indent()} ${symbol.print(term)} $name $duration".padEnd(80, ' ')
  }

  private fun durationMillis(duration: Duration): String {
    return when {
      duration.toMillis() in slow..verySlow -> term.brightYellow("(${duration.toMillis()}ms)")
      duration.toMillis() > verySlow -> term.brightRed("(${duration.toMillis()}ms)")
      else -> ""
    }
  }

  override fun afterSpecClass(klass: KClass<out Spec>, t: Throwable?) {

    n += 1
    val specDesc = Description.spec(klass)

    if (t == null) {
      println("  $n) " + term.brightWhite(klass.qualifiedName!!))
    } else {
      errors = true
      println(term.red(specDesc.name + " *** FAILED ***"))
      println(term.red("  \tcause: ${t.message})"))
    }
    println(" ")

    tests.filter { it.spec::class.qualifiedName == klass.qualifiedName }.forEach {
      val result = results[it.description]
      if (result != null) {
        val line = testLine(it, result)
        println(line)
        when (result.status) {
          TestStatus.Error, TestStatus.Failure -> {
            result.error?.message?.apply {
              println(term.brightRed(it.description.indent() + "  \tcause: $this (${it.source.fileName}:${it.source.lineNumber})"))
            }
          }
        }
      }
    }

    println(" ")
  }

  override fun engineStarted(classes: List<KClass<out Spec>>) {
    start = System.currentTimeMillis()
  }

  override fun enterTestCase(testCase: TestCase) {
    tests.add(testCase)
  }

  override fun exitTestCase(testCase: TestCase, result: TestResult) {
    results[testCase.description] = result
  }
}