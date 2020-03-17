package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import kotlin.jvm.JvmName

/**
 * Returns an [Gen.Exhaustive] which provides the values from the given list.
 */
fun <A> exhaustive(`as`: List<A>): Exhaustive<A> = object : Exhaustive<A>() {
   override val values: List<A> = `as`
}

@JvmName("exhaustiveExt")
fun <A> List<A>.exhaustive(): Exhaustive<A> = object : Exhaustive<A>() {
   override val values: List<A> = this@exhaustive
}
