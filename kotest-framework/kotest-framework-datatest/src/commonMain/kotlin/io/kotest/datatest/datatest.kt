package io.kotest.datatest

import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import io.kotest.core.test.createTestName
import kotlin.jvm.JvmName

/**
 * Registers tests at the root level for each element t of [ts].
 *
 * The test name will be generated from the toString of the element.
 */
fun <T : Any> RootScope.forAll(vararg ts: T, test: suspend TestContext.(T) -> Unit) =
   forAll(ts.toList(), test)

fun <T : Any> RootScope.forAll(ts: List<T>, test: suspend TestContext.(T) -> Unit) {
   val identifiers = Identifiers()
   this.forAll(ts.map { Pair(identifiers.stableIdentifier(it), it) }, test)
}

/**
 * For each (name, element) of [data], will register a top level test, with the first
 * element of the tuple used as the test name.
 */
@JvmName("forAllWithNames")
fun <T : Any> RootScope.forAll(data: List<Pair<String, T>>, test: suspend TestContext.(T) -> Unit) {
   data.forEach { (name, t) ->
      registration().addContainerTest(createTestName(name), false) { test(t) }
   }
}

/**
 * Registers tests inside the given test context for each element of [ts]
 *
 * The test name will be generated from the toString of the element.
 */
suspend fun <T : Any> TestContext.forAll(vararg ts: T, test: suspend TestContext.(T) -> Unit) =
   forAll(ts.toList(), test)

/**
 * Registers tests inside the given test context for each element of [ts]
 *
 * The test name will be generated from the toString of the element.
 */
suspend fun <T : Any> TestContext.forAll(ts: List<T>, test: suspend TestContext.(T) -> Unit) {
   val identifiers = Identifiers()
   ts.forEach { t ->
      val name = identifiers.stableIdentifier(t)
      this.registerTestCase(
         createNestedTest(createTestName(name), false, TestCaseConfig(), TestType.Container, null, null) { test(t) }
      )
   }
}
