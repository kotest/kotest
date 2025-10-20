package io.kotest.datatest

import io.kotest.core.spec.style.scopes.StringSpecRootScope
import io.kotest.core.spec.style.scopes.StringSpecScope
import io.kotest.engine.stable.StableIdents

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> StringSpecRootScope.withData(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend StringSpecScope.(T) -> Unit
) {
   withTests(listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> StringSpecRootScope.withTests(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend StringSpecScope.(T) -> Unit
) {
   withTests(listOf(first, second) + rest, test)
}

@Deprecated(
   "Nested withData is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withData(
   first: T,
   second: T,
   vararg rest: T,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withData is not supported in StringSpec")

@Deprecated(
   "Nested withTests is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withTests(
   first: T,
   second: T,
   vararg rest: T,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withTests is not supported in StringSpec")

fun <T> StringSpecRootScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend StringSpecScope.(T) -> Unit
) {
   withTests(nameFn, listOf(first, second) + rest, test)
}

fun <T> StringSpecRootScope.withTests(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend StringSpecScope.(T) -> Unit
) {
   withTests(nameFn, listOf(first, second) + rest, test)
}

@Deprecated(
   "Nested withData is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withData is not supported in StringSpec")

@Deprecated(
   "Nested withTests is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withTests(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withTests is not supported in StringSpec")

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> StringSpecRootScope.withData(
   ts: Sequence<T>,
   test: suspend StringSpecScope.(T) -> Unit
) {
   withTests(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> StringSpecRootScope.withTests(
   ts: Sequence<T>,
   test: suspend StringSpecScope.(T) -> Unit
) {
   withTests(ts.toList(), test)
}

@Deprecated(
   "Nested withData is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withData(
   ts: Sequence<T>,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withData is not supported in StringSpec")

@Deprecated(
   "Nested withTests is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withTests(
   ts: Sequence<T>,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withTests is not supported in StringSpec")

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> StringSpecRootScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend StringSpecScope.(T) -> Unit
) {
   withTests(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> StringSpecRootScope.withTests(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend StringSpecScope.(T) -> Unit
) {
   withTests(nameFn, ts.toList(), test)
}

@Deprecated(
   "Nested withData is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withData is not supported in StringSpec")

@Deprecated(
   "Nested withTests is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withTests(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withTests is not supported in StringSpec")

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> StringSpecRootScope.withData(
   ts: Iterable<T>,
   test: suspend StringSpecScope.(T) -> Unit
) {
   withTests({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> StringSpecRootScope.withTests(
   ts: Iterable<T>,
   test: suspend StringSpecScope.(T) -> Unit
) {
   withTests({ StableIdents.getStableIdentifier(it) }, ts, test)
}

@Deprecated(
   "Nested withData is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withData(
   ts: Iterable<T>,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withData is not supported in StringSpec")


@Deprecated(
   "Nested withTests is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withTests(
   ts: Iterable<T>,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withTests is not supported in StringSpec")

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> StringSpecRootScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend StringSpecScope.(T) -> Unit
) {
   withTests(nameFn, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> StringSpecRootScope.withTests(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend StringSpecScope.(T) -> Unit
) {
   ts.forEach { t ->
      nameFn(t).invoke { this.test(t) }
   }
}

@Deprecated(
   "Nested withData is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withData is not supported in StringSpec")


@Deprecated(
   "Nested withTests is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withTests(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withTests is not supported in StringSpec")

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> StringSpecRootScope.withData(
   data: Map<String, T>,
   test: suspend StringSpecScope.(T) -> Unit
) {
   withTests(data, test)
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> StringSpecRootScope.withTests(
   data: Map<String, T>,
   test: suspend StringSpecScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      name.invoke { this.test(t) }
   }
}

@Deprecated(
   "Nested withData is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withData(
   data: Map<String, T>,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withData is not supported in StringSpec")

@Deprecated(
   "Nested withTests is not supported inside StringSpec test bodies.",
   level = DeprecationLevel.ERROR
)
fun <T> StringSpecScope.withTests(
   data: Map<String, T>,
   test: suspend StringSpecScope.(T) -> Unit
): Nothing = error("Nested withTests is not supported in StringSpec")
















