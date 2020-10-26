package io.kotest.engine.reporter

import com.github.ajalt.mordant.TermColors
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

/**
 * A base interface for writing test events to the console.
 *
 * For example, see [TeamCityConsoleReporter] which will write out test events in a format that
 * Intellij parses and displays in it's test window.
 */
interface Reporter {

   /**
    * Returns true if at least one test has failed or errored during execution.
    */
   fun hasErrors(): Boolean

   fun engineStarted(classes: List<KClass<out Spec>>)
   fun engineFinished(t: List<Throwable>)

   fun specStarted(kclass: KClass<out Spec>)
   fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>)

   fun testStarted(testCase: TestCase)
   fun testIgnored(testCase: TestCase) {}
   fun testFinished(testCase: TestCase, result: TestResult)
}

interface ConsoleReporter : Reporter {
   fun setTerm(term: TermColors)
}
