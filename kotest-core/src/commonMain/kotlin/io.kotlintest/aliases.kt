@file:JvmName("AssertionAliases")
package io.kotlintest

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.jvm.JvmName

@Deprecated(
   "All packages are now io.kotest",
   ReplaceWith("shouldThrowAnyUnit(block)", "io.kotest.assertions.throwables")
)
inline fun shouldThrowAnyUnit(block: () -> Unit) = io.kotest.assertions.throwables.shouldThrowAnyUnit(block)

@Deprecated("All packages are now io.kotest", ReplaceWith("io.kotest.assertions.throwables.shouldThrow(block)", "io"))
inline fun <reified T : Throwable> shouldThrow(block: () -> Any?): T =
   io.kotest.assertions.throwables.shouldThrow(block)

@Deprecated("All package names are now io.kotest")
infix fun <T, U : T> T.shouldBe(any: U?) = this shouldBe any

@Deprecated("All package names are now io.kotest")
infix fun <T> T.shouldNotBe(any: Any?) = this shouldNotBe any

@Deprecated("All package names are now io.kotest")
fun <T> assertSoftly(block: () -> T): T = io.kotest.assertions.assertSoftly(block)
