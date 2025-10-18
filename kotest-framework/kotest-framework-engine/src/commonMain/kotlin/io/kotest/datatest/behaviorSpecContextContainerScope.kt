package io.kotest.datatest

import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.scopes.BehaviorSpecContextContainerScope
import io.kotest.engine.stable.StableIdents
import kotlin.jvm.JvmName

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
@Deprecated(
   message = "Use withContexts(...) instead.",
   replaceWith = ReplaceWith("withContexts(first, second, *rest, test)"),
   level = DeprecationLevel.WARNING
)
suspend fun <T> BehaviorSpecContextContainerScope.withData(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecContextContainerScope.withContexts(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
@Deprecated(
   message = "Use withContexts(...) instead.",
   replaceWith = ReplaceWith("withContexts(ts, test)"),
   level = DeprecationLevel.WARNING
)
suspend fun <T> BehaviorSpecContextContainerScope.withData(
   ts: Sequence<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecContextContainerScope.withContexts(
   ts: Sequence<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
@Deprecated(
   message = "Use withContexts(...) instead.",
   replaceWith = ReplaceWith("withContexts(ts, test)"),
   level = DeprecationLevel.WARNING
)
suspend fun <T> BehaviorSpecContextContainerScope.withData(
   ts: Iterable<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecContextContainerScope.withContexts(
   ts: Iterable<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
@Deprecated(
   message = "Use withContexts(...) instead.",
   replaceWith = ReplaceWith("withContexts(nameFn, ts, test)"),
   level = DeprecationLevel.WARNING
)
suspend fun <T> BehaviorSpecContextContainerScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecContextContainerScope.withContexts(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
@Deprecated(
   message = "Use withContexts(...) instead.",
   replaceWith = ReplaceWith("withContexts(nameFn, first, second, rest, test)"),
   level = DeprecationLevel.WARNING
)
suspend fun <T> BehaviorSpecContextContainerScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecContextContainerScope.withContexts(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
@Deprecated(
   message = "Use withContexts(...) instead.",
   replaceWith = ReplaceWith("withContexts(nameFn, ts, test)"),
   level = DeprecationLevel.WARNING
)
suspend fun <T> BehaviorSpecContextContainerScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecContextContainerScope.withContexts(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      context(nameFn(t), false) { this.test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
@KotestTestScope
@Deprecated(
   message = "Use withContexts(...) instead.",
   replaceWith = ReplaceWith("withContexts(data, test)"),
   level = DeprecationLevel.WARNING
)
suspend fun <T> BehaviorSpecContextContainerScope.withData(
   data: Map<String, T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(data, test)
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withContextsMap")
@KotestTestScope
suspend fun <T> BehaviorSpecContextContainerScope.withContexts(
   data: Map<String, T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) -> context(name, false) { this.test(t) } }
}
