package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.spec.style.scopes.addContainer
import io.kotest.core.test.Identifiers
import kotlin.jvm.JvmName

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
fun <T : Any> RootScope.withData(first: T, second: T, vararg rest: T, test: suspend ContainerScope.(T) -> Unit) {
   withData(listOf(first, second) + rest, test)
}

@ExperimentalKotest
fun <T : Any> RootScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend ContainerScope.(T) -> Unit
) = withData(nameFn, listOf(first, second) + rest, test)

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
fun <T : Any> RootScope.withData(ts: Sequence<T>, test: suspend ContainerScope.(T) -> Unit) {
   withData(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
fun <T : Any> RootScope.withData(nameFn: (T) -> String, ts: Sequence<T>, test: suspend ContainerScope.(T) -> Unit) {
   withData(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@ExperimentalKotest
fun <T : Any> RootScope.withData(ts: Collection<T>, test: suspend ContainerScope.(T) -> Unit) {
   withData({ getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
@ExperimentalKotest
fun <T : Any> RootScope.withData(
   nameFn: (T) -> String,
   ts: Collection<T>,
   test: suspend ContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      addContainer(TestName(nameFn(t)), false, null) { test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
@ExperimentalKotest
@JvmName("withDataMap")
fun <T : Any> RootScope.withData(data: Map<String, T>, test: suspend ContainerScope.(T) -> Unit) {
   data.forEach { (name, t) ->
      addContainer(TestName(name), false, null) { test(t) }
   }
}
