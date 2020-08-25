package io.kotest.matchers

import kotlin.reflect.KClass

@Deprecated("Moved to io.kotest.mathers.types. Will be removed in 4.3",
   ReplaceWith("io.kotest.matchers.types.instanceOf(expected)", "io"))
fun instanceOf(expected: KClass<*>): Matcher<Any?> = io.kotest.matchers.types.instanceOf(expected)

@Deprecated("Moved to io.kotest.mathers.types. Will be removed in 4.3",
   ReplaceWith("io.kotest.matchers.types.beInstanceOf(expected)", "io"))
fun beInstanceOf(expected: KClass<*>): Matcher<Any?> = io.kotest.matchers.types.beInstanceOf(expected)

@Deprecated("Moved to io.kotest.mathers.types. Will be removed in 4.3",
   ReplaceWith("io.kotest.matchers.types.beTheSameInstanceAs(ref)", "io"))
fun <T> beTheSameInstanceAs(ref: T): Matcher<T> = io.kotest.matchers.types.beTheSameInstanceAs(ref)

@Deprecated("Moved to io.kotest.mathers.types. Will be removed in 4.3",
   ReplaceWith("io.kotest.matchers.types.beInstanceOf(T::class)", "io"))
inline fun <reified T : Any> beInstanceOf(): Matcher<Any?> = io.kotest.matchers.types.beInstanceOf(T::class)

@Deprecated("Moved to io.kotest.mathers.types. Will be removed in 4.3",
   ReplaceWith("io.kotest.matchers.types.beOfType(expected)", "io"))
fun beOfType(expected: KClass<*>): Matcher<Any?> = io.kotest.matchers.types.beOfType(expected)

@Deprecated("Moved to io.kotest.mathers.types. Will be removed in 4.3",
   ReplaceWith("io.kotest.matchers.types.beOfType(T::class)", "io"))
inline fun <reified T : Any> beOfType(): Matcher<Any?> = io.kotest.matchers.types.beOfType(T::class)
