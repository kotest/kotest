package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.scopes.RootContext
import io.kotest.core.test.Identifiers
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import io.kotest.core.test.createTestName
import kotlin.jvm.JvmName

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
fun <T : Any> RootContext.withData(first: T, second: T, vararg rest: T, test: suspend TestContext.(T) -> Unit) =
   withData(listOf(first, second) + rest, test)

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
fun <T : Any> RootContext.withData(ts: Sequence<T>, test: suspend TestContext.(T) -> Unit) {
   withData(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
fun <T : Any> RootContext.withData(ts: Collection<T>, test: suspend TestContext.(T) -> Unit) {
   ts.forEach { t ->
      val name = Identifiers.stableIdentifier(t)
      registration().addContainerTest(createTestName(name), false) { test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
@ExperimentalKotest
@JvmName("forAllWithNames")
fun <T : Any> RootContext.withData(data: Map<String, T>, test: suspend TestContext.(T) -> Unit) {
   data.forEach { (name, t) ->
      registration().addContainerTest(createTestName(name), false) { test(t) }
   }
}

/**
 * Registers tests inside the given test context for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
suspend fun <T : Any> TestContext.withData(ts: Sequence<T>, test: suspend TestContext.(T) -> Unit) {
   withData(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
suspend fun <T : Any> TestContext.withData(first: T, second: T, vararg rest: T, test: suspend TestContext.(T) -> Unit) =
   withData(listOf(first, second) + rest, test)

/**
 * Registers tests inside the given test context for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
suspend fun <T : Any> TestContext.withData(ts: Collection<T>, test: suspend TestContext.(T) -> Unit) {
   ts.forEach { t ->
      val name = Identifiers.stableIdentifier(t)
      this.registerTestCase(
         createNestedTest(createTestName(name), false, TestCaseConfig(), TestType.Container, null, null) { test(t) }
      )
   }
}


/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@ExperimentalKotest
@JvmName("forAllWithNames")
suspend fun <T : Any> TestContext.withData(data: Map<String, T>, test: suspend TestContext.(T) -> Unit) {
   data.forEach { (name, t) ->
      this.registerTestCase(
         createNestedTest(createTestName(name), false, TestCaseConfig(), TestType.Container, null, null) { test(t) }
      )
   }
}
