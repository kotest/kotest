package io.kotest.engine.reports

import io.kotest.common.reflection.bestName
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import kotlin.reflect.KClass
import kotlin.time.Clock

/**
 * @target the Kotlin KMP target this report is generated for. If set will be prefixed to each test name.
 */
class JUnitXmlReportGenerator(
   private val clock: Clock,
   private val includeStackTraces: Boolean,
   private val hostname: String?,
   private val target: String?,
) {

   private val xml = XML {
      indentString = "  "
      xmlVersion = XmlVersion.XML10
   }

   fun xml(spec: KClass<*>, tests: Map<TestCase, TestResult>): String {
      val testsuite = generate(spec, tests)
      return xml.encodeToString(TestSuite.serializer(), testsuite)
   }

   private fun generate(spec: KClass<*>, tests: Map<TestCase, TestResult>): TestSuite {
      return TestSuite(
         name = spec.bestName(),
         tests = tests.size,
         failures = tests.filter { it.value.isFailure }.count(),
         errors = tests.filter { it.value.isError }.count(),
         skipped = tests.filter { it.value.isIgnored }.count(),
         timestamp = clock.now().toString().substringBeforeLast("."), // time without nanos
         hostname = hostname ?: "",
         time = tests.map { it.value.duration.inWholeMilliseconds / 1_000.0 }.sum(),
         cases = tests.map { (test, result) ->
            val name = if (target == null) test.descriptor.path().value else "[${target}] ${test.descriptor.path().value}"
            TestCaseElement(
               classname = spec.bestName(),
               name = name,
               time = result.duration.inWholeMilliseconds / 1_000.0,
               error = errorElementOrNull(result),
               failure = failureElementOrNull(result),
               skipped = skippedElementOrNull(result),
            )
         }
      )
   }

   private fun failureElementOrNull(result: TestResult): FailureElement? {
      return when (result) {
         is TestResult.Failure -> FailureElement(
            message = result.cause.message ?: "",
            type = result.cause::class.simpleName ?: "",
            stack = if (includeStackTraces) result.cause.stackTraceToString() else null,
         )

         else -> null
      }
   }

   private fun errorElementOrNull(result: TestResult): ErrorElement? {
      return when (result) {
         is TestResult.Error -> ErrorElement(
            message = result.cause.message ?: "",
            type = result.cause::class.simpleName ?: "",
            stack = if (includeStackTraces) result.cause.stackTraceToString() else null,
         )

         else -> null
      }
   }

   private fun skippedElementOrNull(result: TestResult): SkippedElement? {
      return when (result) {
         is TestResult.Ignored -> SkippedElement(message = result.reason)
         else -> null
      }
   }

}
