package io.kotest.engine.reporter

import com.github.ajalt.mordant.TermColors
import io.kotest.core.execution.ExecutionContext
import io.kotest.core.plan.Descriptor
import io.kotest.core.plan.DescriptorId
import io.kotest.core.plan.fileNameOrUnknown
import io.kotest.core.plan.lineNumberOrDefault
import io.kotest.core.plan.toDescriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestNameFormatter
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType
import io.kotest.engine.spec.SpecNameFormatter
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

class MochaConsoleReporter(
   private val context: ExecutionContext,
   private val term: TermColors,
   private val slow: Int = 1000,
   private val verySlow: Int = 3000
) : Reporter {

   private val formatter = TestNameFormatter(context.configuration)

   private val margin = " ".repeat(2)

   private val tests = mutableListOf<TestCase>()
   private val results = mutableMapOf<DescriptorId, Pair<TestCase, TestResult>>()
   private var n = 0
   private var start = 0L
   private var errors = false

   private fun Descriptor.TestDescriptor.indent(): String = "\t".repeat(depth())

   override fun hasErrors(): Boolean = errors

   private fun testLine(testCase: TestCase, result: TestResult): String {

      val name = when (result.status) {
         TestStatus.Failure, TestStatus.Error -> term.brightRed("${testCase.descriptor.displayName.value} *** FAILED ***")
         TestStatus.Success -> testCase.descriptor.displayName.value
         TestStatus.Ignored -> term.gray("${testCase.descriptor.displayName.value} ??? IGNORED ???")
      }
      val symbol = when (result.status) {
         TestStatus.Success -> SuccessSymbol
         TestStatus.Error, TestStatus.Failure -> FailureSymbol
         TestStatus.Ignored -> IgnoredSymbol
      }
      val duration = when (testCase.type) {
         TestType.Test -> durationString(result.duration)
         else -> ""
      }
      return "$margin${testCase.descriptor.indent()} ${symbol.print(term)} $name $duration".padEnd(80, ' ')
   }

   private fun durationString(durationMs: Long): String {
      return when {
         durationMs in slow..verySlow -> term.brightYellow("(${durationMs}ms)")
         durationMs > verySlow -> term.brightRed("(${durationMs}ms)")
         else -> ""
      }
   }

   private fun cause(testCase: TestCase, message: String?): String {
      return term.brightRed("$margin\tcause: $message (${testCase.source.fileNameOrUnknown()}:${testCase.source.lineNumberOrDefault()})")
   }

   override fun specStarted(kclass: KClass<*>) {}

   override fun specFinished(kclass: KClass<*>, t: Throwable?, results: Map<TestCase, TestResult>) {

      n += 1

      if (t == null) {
         println("$margin$n) " + term.brightWhite(kclass.qualifiedName!!))
      } else {
         errors = true
         print(margin)
         println(term.red("${SpecNameFormatter().format(kclass)} *** FAILED ***"))
         println(term.red("$margin\tcause: ${t.message})"))
      }
      println()

      tests
         .filter { it.spec::class.qualifiedName == kclass.qualifiedName }
         .forEach { testCase ->
            val result = results[testCase]
            if (result != null) {
               val line = testLine(testCase, result)
               println(line)
               when (result.status) {
                  TestStatus.Error, TestStatus.Failure -> {
                     errors = true
                     println()
                     println(cause(testCase, result.error?.message))
                     println()
                  }
                  else -> {
                  }
               }
            }
         }

      println()
   }

   override fun engineStarted(classes: List<KClass<*>>) {
      start = System.currentTimeMillis()
   }

   override fun testStarted(testCase: TestCase) {
      tests.add(testCase)
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      results[testCase.descriptor.id] = Pair(testCase, result)
   }

   private fun padNewLines(str: String, pad: String): String = str.lines().joinToString("\n") { "$pad$it" }

   override fun engineFinished(t: List<Throwable>) {

      val duration = (System.currentTimeMillis() - start)

      val ignored = results.filter { it.value.second.status == TestStatus.Ignored }
      val failed = results.filter { it.value.second.status in listOf(TestStatus.Failure, TestStatus.Error) }
      val passed = results.filter { it.value.second.status == TestStatus.Success }

      val specs = tests.map { it.spec::class.toDescriptor() }.distinct()
      val specDistinctCount = specs.distinct().size

      println()
      println(term.brightWhite("${margin}Kotest completed in ${duration / 1000} seconds / $duration milliseconds"))
      println("${margin}Executed $specDistinctCount specs containing ${failed.size + passed.size + ignored.size} tests")
      println("$margin${passed.size} passed, ${failed.size} failed, ${ignored.size} ignored")
      if (failed.isNotEmpty()) {
         println()
         println(term.brightWhite("$margin----------------------------- ${failed.size} FAILURES -----------------------------"))
         failed.forEach {

            val displayName = it.value.first.descriptor.displayPath(false).value

            println()
            println("$margin${term.brightRed(if (isWindows) "X" else "✘")} ${term.brightWhite(displayName)}")
            println()
            val error = it.value.second.error
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
