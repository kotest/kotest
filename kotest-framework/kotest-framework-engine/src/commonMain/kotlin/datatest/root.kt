package datatest

import io.kotest.core.names.TestName
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.spec.style.scopes.addTest
import io.kotest.core.test.TestType
import kotlin.jvm.JvmName

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdentifiers].
 */
fun <T> RootScope.withData(first: T, second: T, vararg rest: T, test: suspend ContainerScope.(T) -> Unit) {
   withData(listOf(first, second) + rest, test)
}

fun <T> RootScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend ContainerScope.(T) -> Unit
) = withData(nameFn, listOf(first, second) + rest, test)

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdentifiers].
 */
fun <T> RootScope.withData(ts: Sequence<T>, test: suspend ContainerScope.(T) -> Unit) {
   withData(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdentifiers].
 */
fun <T> RootScope.withData(nameFn: (T) -> String, ts: Sequence<T>, test: suspend ContainerScope.(T) -> Unit) {
   withData(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdentifiers].
 */
fun <T> RootScope.withData(ts: Iterable<T>, test: suspend ContainerScope.(T) -> Unit) {
   withData({ getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> RootScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend ContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      addTest(TestName(nameFn(t)), false, null, TestType.Dynamic) { test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
fun <T> RootScope.withData(data: Map<String, T>, test: suspend ContainerScope.(T) -> Unit) {
   data.forEach { (name, t) ->
      addTest(TestName(name), false, null, TestType.Dynamic) { test(t) }
   }
}
