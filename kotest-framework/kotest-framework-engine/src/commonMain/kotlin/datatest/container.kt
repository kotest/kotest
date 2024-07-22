package datatest

import io.kotest.core.names.TestName
import io.kotest.core.spec.style.scopes.AbstractContainerScope
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.test.TestType
import kotlin.jvm.JvmName

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdentifiers].
 */
suspend fun <T> ContainerScope.withData(
   first: T,
   second: T,
   vararg rest: T,
   test: suspend ContainerScope.(T) -> Unit
) = // we need first and second to help the compiler disambiguate
   withData(listOf(first, second) + rest, test)

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdentifiers].
 */
suspend fun <T> ContainerScope.withData(
   ts: Sequence<T>,
   test: suspend ContainerScope.(T) -> Unit
) = withData(ts.toList(), test)

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdentifiers].
 */
suspend fun <T> ContainerScope.withData(
   ts: Iterable<T>,
   test: suspend ContainerScope.(T) -> Unit
) {
   withData({ getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ContainerScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend ContainerScope.(T) -> Unit
) = withData(nameFn, ts.toList(), test)

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ContainerScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend ContainerScope.(T) -> Unit
) = withData(nameFn, listOf(first, second) + rest, test)

/**
 * Registers tests inside the given [ContainerScope] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ContainerScope.withData(
   nameFn: (T) -> String,
   @BuilderInference ts: Iterable<T>,
   @BuilderInference test: suspend ContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      registerTest(TestName(nameFn(t)), false, null, TestType.Dynamic) { AbstractContainerScope(this).test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
suspend fun <T> ContainerScope.withData(data: Map<String, T>, test: suspend ContainerScope.(T) -> Unit) {
   data.forEach { (name, t) ->
      registerTest(TestName(name), false, null, TestType.Dynamic) { AbstractContainerScope(this).test(t) }
   }
}
