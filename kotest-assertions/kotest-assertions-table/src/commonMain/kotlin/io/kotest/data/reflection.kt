@file:Suppress("DEPRECATION")

package io.kotest.data

/**
 * Returns the names of the parameters if supported. Eg, for `fun foo(a: String, b: Boolean)` on the JVM
 * it would return a, b and on unsupported platforms an empty list.
 */
expect fun paramNames(fn: Function<*>): List<String>
