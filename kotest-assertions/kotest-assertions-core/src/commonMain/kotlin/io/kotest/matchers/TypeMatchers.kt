package io.kotest.matchers

import kotlin.reflect.KClass

@Deprecated("Moved to io.kotest.mathers.types. Will be removed in 4.6",
   ReplaceWith("io.kotest.matchers.types.instanceOf(expected)", "io"), level = DeprecationLevel.ERROR)
fun instanceOf(expected: KClass<*>): Matcher<Any?> = io.kotest.matchers.types.instanceOf(expected)

@Deprecated("Moved to io.kotest.mathers.types. Will be removed in 4.6",
   ReplaceWith("io.kotest.matchers.types.beInstanceOf(expected)", "io"), level = DeprecationLevel.ERROR)
fun beInstanceOf(expected: KClass<*>): Matcher<Any?> = io.kotest.matchers.types.beInstanceOf(expected)

@Deprecated("Moved to io.kotest.mathers.types. Will be removed in 4.6",
   ReplaceWith("io.kotest.matchers.types.beTheSameInstanceAs(ref)", "io"), level = DeprecationLevel.ERROR)
fun <T> beTheSameInstanceAs(ref: T): Matcher<T> = io.kotest.matchers.types.beTheSameInstanceAs(ref)

@Deprecated("Moved to io.kotest.mathers.types. Will be removed in 4.6",
   ReplaceWith("io.kotest.matchers.types.beInstanceOf(T::class)", "io"), level = DeprecationLevel.ERROR)
inline fun <reified T : Any> beInstanceOf(): Matcher<Any?> = io.kotest.matchers.types.beInstanceOf(T::class)

@Deprecated("Moved to io.kotest.mathers.types. Will be removed in 4.6",
   ReplaceWith("io.kotest.matchers.types.beOfType(expected)", "io"), level = DeprecationLevel.ERROR)
fun beOfType(expected: KClass<*>): Matcher<Any?> = io.kotest.matchers.types.beOfType(expected)

@Deprecated("Moved to io.kotest.mathers.types. Will be removed in 4.6",
   ReplaceWith("io.kotest.matchers.types.beOfType(T::class)", "io"), level = DeprecationLevel.ERROR)
inline fun <reified T : Any> beOfType(): Matcher<Any?> = io.kotest.matchers.types.beOfType(T::class)
