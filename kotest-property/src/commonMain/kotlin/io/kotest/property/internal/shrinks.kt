package io.kotest.property.internal

import io.kotest.fp.Tuple2
import io.kotest.fp.Tuple3
import io.kotest.property.ArgumentValue
import io.kotest.property.PropertyContext

suspend fun <A> shrink(
   a: ArgumentValue<A>,
   property: suspend PropertyContext.(A) -> Unit
): A {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   return with(context) {
      shrink(a.shrinks) { property(it) }
   }
}

// shrinks a single set of failed inputs returning a tuple of the smallest values
suspend fun <A, B> shrink(
   a: ArgumentValue<A>,
   b: ArgumentValue<B>,
   property: suspend PropertyContext.(A, B) -> Unit
): Tuple2<A, B> {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   return with(context) {
      val smallestA = shrink(a.shrinks) { property(it, b.value) }
      val smallestB = shrink(b.shrinks) { property(a.value, it) }
      Tuple2(smallestA, smallestB)
   }
}

suspend fun <A, B, C> shrink(
   a: ArgumentValue<A>,
   b: ArgumentValue<B>,
   c: ArgumentValue<C>,
   property: suspend PropertyContext.(A, B, C) -> Unit
): Tuple3<A, B, C> {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   return with(context) {
      val smallestA = shrink(a.shrinks) { property(it, b.value, c.value) }
      val smallestB = shrink(b.shrinks) { property(a.value, it, c.value) }
      val smallestC = shrink(c.shrinks) { property(a.value, b.value, it) }
      Tuple3(smallestA, smallestB, smallestC)
   }
}
