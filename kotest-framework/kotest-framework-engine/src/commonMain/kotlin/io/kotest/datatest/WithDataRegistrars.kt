package io.kotest.datatest

import io.kotest.core.spec.style.scopes.TerminalScope
import io.kotest.core.test.TestScope
import io.kotest.engine.stable.StableIdents
import kotlin.jvm.JvmName


/**
 * Generic capability to register a (root-level) test that runs in receiver C.
 */
interface WithDataRootRegistrar<C : TestScope> {
   fun registerWithDataTest(name: String, test: suspend C.() -> Unit)
}

/**
 * Generic capability to register a (container-level) test that runs in receiver C.
 */
interface WithDataContainerRegistrar<C : TestScope> {
   suspend fun registerWithDataTest(name: String, test: suspend C.() -> Unit)
}

/**
 * Generic capability to disallow register a (container-level) test that runs in receiver C.
 */
interface WithDataTerminalRegistrar<C : TerminalScope> {
   companion object {
      const val ERROR_MESSAGE = "Nested withData is not supported for this spec style."
   }
}

/**
 * Registers tests at the root level for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T, C : TestScope> WithDataRootRegistrar<C>.withData(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend C.(T) -> Unit
) {
   withData(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T, C : TestScope> WithDataContainerRegistrar<C>.withData(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend C.(T) -> Unit
) {
   withData(listOf(first, second) + rest, test)
}

/**
 * Disallows to register tests inside the given test context for each element.
 */
@Deprecated(
   WithDataTerminalRegistrar.ERROR_MESSAGE,
   level = DeprecationLevel.ERROR
)
fun <T, C : TerminalScope> WithDataTerminalRegistrar<C>.withData(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend C.(T) -> Unit
): Nothing = error(WithDataTerminalRegistrar.ERROR_MESSAGE)

/**
 * Registers tests at the root level for each element.
 * The test name will be generated from the given [nameFn] function.
 */
fun <T, C : TestScope> WithDataRootRegistrar<C>.withData(
   nameFn: (T) -> String,
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend C.(T) -> Unit
) {
   withData(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T, C : TestScope> WithDataContainerRegistrar<C>.withData(
   nameFn: (T) -> String,
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend C.(T) -> Unit
) {
   withData(nameFn, listOf(first, second) + rest, test)
}

/**
 * Disallows to register tests inside the given test context for each element.
 */
@Deprecated(
   WithDataTerminalRegistrar.ERROR_MESSAGE,
   level = DeprecationLevel.ERROR
)
fun <T, C : TerminalScope> WithDataTerminalRegistrar<C>.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend C.(T) -> Unit
): Nothing = error(WithDataTerminalRegistrar.ERROR_MESSAGE)

/**
 * Registers tests at the root level for each element of [ts].
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T, C : TestScope> WithDataRootRegistrar<C>.withData(
   ts: Sequence<T>,
   test: suspend C.(T) -> Unit
) {
   withData(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T, C : TestScope> WithDataContainerRegistrar<C>.withData(
   ts: Sequence<T>,
   test: suspend C.(T) -> Unit
) {
   withData(ts.toList(), test)
}

/**
 * Disallows to register tests inside the given test context for each element of [ts].
 */
@Deprecated(
   WithDataTerminalRegistrar.ERROR_MESSAGE,
   level = DeprecationLevel.ERROR
)
fun <T, C : TerminalScope> WithDataTerminalRegistrar<C>.withData(
   ts: Sequence<T>,
   test: suspend C.(T) -> Unit
): Nothing = error(WithDataTerminalRegistrar.ERROR_MESSAGE)

/**
 * Registers tests at the root level for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
fun <T, C : TestScope> WithDataRootRegistrar<C>.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend C.(T) -> Unit
) {
   withData(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T, C : TestScope> WithDataContainerRegistrar<C>.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend C.(T) -> Unit
) {
   withData(nameFn, ts.toList(), test)
}

/**
 * Disallows to register tests inside the given test context for each element of [ts].
 */
@Deprecated(
   WithDataTerminalRegistrar.ERROR_MESSAGE,
   level = DeprecationLevel.ERROR
)
fun <T, C : TerminalScope> WithDataTerminalRegistrar<C>.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend C.(T) -> Unit
): Nothing = error(WithDataTerminalRegistrar.ERROR_MESSAGE)

/**
 * Registers tests at the root level for each element of [ts].
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T, C : TestScope> WithDataRootRegistrar<C>.withData(
   ts: Iterable<T>,
   test: suspend C.(T) -> Unit
) {
   withData({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T, C : TestScope> WithDataContainerRegistrar<C>.withData(
   ts: Iterable<T>,
   test: suspend C.(T) -> Unit
) {
   withData({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Disallows to register tests inside the given test context for each element of [ts].
 */
@Deprecated(
   WithDataTerminalRegistrar.ERROR_MESSAGE,
   level = DeprecationLevel.ERROR
)
fun <T, C : TerminalScope> WithDataTerminalRegistrar<C>.withData(
   ts: Iterable<T>,
   test: suspend C.(T) -> Unit
): Nothing = error(WithDataTerminalRegistrar.ERROR_MESSAGE)

/**
 * Registers tests at the root level for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
fun <T, C : TestScope> WithDataRootRegistrar<C>.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend C.(T) -> Unit
) {
   ts.forEach { t ->
      registerWithDataTest(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T, C : TestScope> WithDataContainerRegistrar<C>.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend C.(T) -> Unit
) {
   ts.forEach { t ->
      registerWithDataTest(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
fun <T, C : TestScope> WithDataRootRegistrar<C>.withData(
   data: Map<String, T>,
   test: suspend C.(T) -> Unit
) {
   data.forEach { (name, t) ->
      registerWithDataTest(name) { this.test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
suspend fun <T, C : TestScope> WithDataContainerRegistrar<C>.withData(
   data: Map<String, T>,
   test: suspend C.(T) -> Unit
) {
   data.forEach { (name, t) ->
      registerWithDataTest(name) { this.test(t) }
   }
}
