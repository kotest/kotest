package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.descriptors.append
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.scopes.RootContext
import io.kotest.core.test.Identifiers
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import kotlin.jvm.JvmName

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
fun <T : Any> RootContext.withData(first: T, second: T, vararg rest: T, test: suspend TestContext.(T) -> Unit) =
   withData(listOf(first, second) + rest, test)

@ExperimentalKotest
fun <T : Any> RootContext.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend TestContext.(T) -> Unit
) = withData(nameFn, listOf(first, second) + rest, test)

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
fun <T : Any> RootContext.withData(ts: Sequence<T>, test: suspend TestContext.(T) -> Unit) =
   withData(ts.toList(), test)

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
fun <T : Any> RootContext.withData(nameFn: (T) -> String, ts: Sequence<T>, test: suspend TestContext.(T) -> Unit) =
   withData(nameFn, ts.toList(), test)

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
fun <T : Any> RootContext.withData(ts: Collection<T>, test: suspend TestContext.(T) -> Unit) =
   withData({ getStableIdentifier(it) }, ts, test)

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
@ExperimentalKotest
fun <T : Any> RootContext.withData(nameFn: (T) -> String, ts: Collection<T>, test: suspend TestContext.(T) -> Unit) {
   ts.forEach { t ->
      registration().addContainerTest(TestName(nameFn(t)), false) { test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
@ExperimentalKotest
@JvmName("withDataMap")
fun <T : Any> RootContext.withData(data: Map<String, T>, test: suspend TestContext.(T) -> Unit) {
   data.forEach { (name, t) ->
      registration().addContainerTest(TestName(name), false) { test(t) }
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
 * Registers tests inside the given test context for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
@ExperimentalKotest
suspend fun <T : Any> TestContext.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend TestContext.(T) -> Unit
) {
   withData(nameFn, ts.toList(), test)
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
 * Registers tests inside the given test context for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
suspend fun <T : Any> TestContext.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend TestContext.(T) -> Unit
) = withData(nameFn, listOf(first, second) + rest, test)

/**
 * Registers tests inside the given test context for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
suspend fun <T : Any> TestContext.withData(ts: Collection<T>, test: suspend TestContext.(T) -> Unit) {
   withData({ getStableIdentifier(it) }, ts, test)
}


/**
 * Registers tests inside the given test context for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
suspend fun <T : Any> TestContext.withData(
   nameFn: (T) -> String,
   @BuilderInference ts: Collection<T>,
   @BuilderInference test: suspend TestContext.(T) -> Unit
) {
   ts.forEach { t ->
      val name = nameFn(t)
      this.registerTestCase(
         createNestedTest(
            descriptor = testCase.descriptor.append(name),
            name = TestName(name),
            xdisabled = false,
            config = TestCaseConfig(),
            type = TestType.Container,
            factoryId = null
         ) { test(t) }
      )
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@ExperimentalKotest
@JvmName("withDataMap")
suspend fun <T : Any> TestContext.withData(data: Map<String, T>, test: suspend TestContext.(T) -> Unit) {
   data.forEach { (name, t) ->
      this.registerTestCase(
         createNestedTest(
            descriptor = testCase.descriptor.append(name),
            name = TestName(name),
            xdisabled = false,
            config = TestCaseConfig(),
            type = TestType.Container,
            factoryId = null,
         ) { test(t) }
      )
   }
}
