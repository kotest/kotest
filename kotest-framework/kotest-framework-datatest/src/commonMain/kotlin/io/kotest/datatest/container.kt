package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.descriptors.append
import io.kotest.core.names.TestName
import io.kotest.core.test.Identifiers
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import kotlin.jvm.JvmName

/**
 * Registers tests inside the given test context for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
suspend fun <T : Any> TestContext.withData(
   first: T,
   second: T,
   vararg rest: T,
   test: suspend TestContext.(T) -> Unit
) = // we need first and second to help the compiler disambiguate
   withData(listOf(first, second) + rest, test)

/**
 * Registers tests inside the given test context for each element of [ts].
 *
 * The test names will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
suspend fun <T : Any> TestContext.withData(
   ts: Sequence<T>,
   test: suspend TestContext.(T) -> Unit
) = withData(ts.toList(), test)

/**
 * Registers tests inside the given test context for each element of [ts].
 *
 * The test names will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
suspend fun <T : Any> TestContext.withData(
   ts: Iterable<T>,
   test: suspend TestContext.(T) -> Unit
) {
   withData({ getStableIdentifier(it) }, ts, test)
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
) = withData(nameFn, ts.toList(), test)

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
suspend fun <T : Any> TestContext.withData(
   nameFn: (T) -> String,
   @BuilderInference ts: Iterable<T>,
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
