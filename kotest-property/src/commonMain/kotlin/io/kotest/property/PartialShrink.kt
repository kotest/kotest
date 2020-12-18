package io.kotest.property

import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.next
import kotlin.reflect.KProperty1

fun <T> Arb<T>.shrinkWith(
   shrinker: Shrinker<T>
) = arbitrary(edgecases(), shrinker) { next(it) }


infix fun <Original, T> KProperty1<Original, T>.shrinkWith(
   shrinker: Shrinker<T>
) = PartialShrinker<Original, T>(
   { this.get(it) },
   shrinker
)

infix fun <Original, V> ShrinkerSelectPart<Original, V>.shrinkWith(
   shrinker: Shrinker<V>
) = PartialShrinker(
   selection,
   shrinker
)

class ShrinkerSelectPart<Original, V>(
   val selection: (Original) -> V
)


fun <Original, A> ShrinkerBuilder<Original>.build(
   partialShrinkerA: PartialShrinker<Original, A>,
   buildFn: Original.(A) -> Original
): Shrinker<Original> =
   partialShrinkerA.build(buildFn)



fun <Original, A, B> ShrinkerBuilder<Original>.build(
   partialShrinkerA: PartialShrinker<Original, A>,
   partialShrinkerB: PartialShrinker<Original, B>,
   buildFn: Original.(A, B) -> Original
): Shrinker<Original> =
   partialShrinkerA.bind(partialShrinkerB).build { (a, b) ->
      buildFn(a, b)
   }


fun <Original, A, B, C> ShrinkerBuilder<Original>.build(
   partialShrinkerA: PartialShrinker<Original, A>,
   partialShrinkerB: PartialShrinker<Original, B>,
   partialShrinkerC: PartialShrinker<Original, C>,
   buildFn: Original.(A, B, C) -> Original
): Shrinker<Original> = partialShrinkerA.bind(partialShrinkerB).bind(partialShrinkerC)
   .build { (ab, c) ->
      val (a, b) = ab
      buildFn(a, b, c)
   }


fun <Original, A, B, C, D> ShrinkerBuilder<Original>.build(
   partialShrinkerA: PartialShrinker<Original, A>,
   partialShrinkerB: PartialShrinker<Original, B>,
   partialShrinkerC: PartialShrinker<Original, C>,
   partialShrinkerD: PartialShrinker<Original, D>,
   buildFn: Original.(A, B, C, D) -> Original
): Shrinker<Original> = partialShrinkerA.bind(partialShrinkerB).bind(partialShrinkerC)
   .bind(partialShrinkerD).build { (abc, d) ->
      val (ab, c) = abc
      val (a, b) = ab
      buildFn(a, b, c, d)
   }

fun <Original, A, B, C, D, E> ShrinkerBuilder<Original>.build(
   partialShrinkerA: PartialShrinker<Original, A>,
   partialShrinkerB: PartialShrinker<Original, B>,
   partialShrinkerC: PartialShrinker<Original, C>,
   partialShrinkerD: PartialShrinker<Original, D>,
   partialShrinkerE: PartialShrinker<Original, E>,
   buildFn: Original.(A, B, C, D, E) -> Original
): Shrinker<Original> =
   partialShrinkerA.bind(partialShrinkerB).bind(partialShrinkerC).bind(partialShrinkerD)
      .bind(partialShrinkerE).build { (abcd, e) ->
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         this.buildFn(a, b, c, d, e)
      }

fun <Original, A, B, C, D, E, F> ShrinkerBuilder<Original>.build(
   partialShrinkerA: PartialShrinker<Original, A>,
   partialShrinkerB: PartialShrinker<Original, B>,
   partialShrinkerC: PartialShrinker<Original, C>,
   partialShrinkerD: PartialShrinker<Original, D>,
   partialShrinkerE: PartialShrinker<Original, E>,
   partialShrinkerF: PartialShrinker<Original, F>,
   buildFn: Original.(A, B, C, D, E, F) -> Original
): Shrinker<Original> =
   partialShrinkerA.bind(partialShrinkerB).bind(partialShrinkerC).bind(partialShrinkerD)
      .bind(partialShrinkerE).bind(partialShrinkerF).build { (abcde, f) ->
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         buildFn(a, b, c, d, e, f)
      }

fun <Original, A, B, C, D, E, F, G> ShrinkerBuilder<Original>.build(
   partialShrinkerA: PartialShrinker<Original, A>,
   partialShrinkerB: PartialShrinker<Original, B>,
   partialShrinkerC: PartialShrinker<Original, C>,
   partialShrinkerD: PartialShrinker<Original, D>,
   partialShrinkerE: PartialShrinker<Original, E>,
   partialShrinkerF: PartialShrinker<Original, F>,
   partialShrinkerG: PartialShrinker<Original, G>,
   buildFn: Original.(A, B, C, D, E, F, G) -> Original
): Shrinker<Original> = partialShrinkerA.bind(partialShrinkerB).bind(partialShrinkerC)
   .bind(partialShrinkerD).bind(partialShrinkerE).bind(partialShrinkerF)
   .bind(partialShrinkerG).build { (abcdef, g) ->
      val (abcde, f) = abcdef
      val (abcd, e) = abcde
      val (abc, d) = abcd
      val (ab, c) = abc
      val (a, b) = ab
      buildFn(a, b, c, d, e, f, g)
   }

fun <Original, A, B, C, D, E, F, G, H> ShrinkerBuilder<Original>.build(
   partialShrinkerA: PartialShrinker<Original, A>,
   partialShrinkerB: PartialShrinker<Original, B>,
   partialShrinkerC: PartialShrinker<Original, C>,
   partialShrinkerD: PartialShrinker<Original, D>,
   partialShrinkerE: PartialShrinker<Original, E>,
   partialShrinkerF: PartialShrinker<Original, F>,
   partialShrinkerG: PartialShrinker<Original, G>,
   partialShrinkerH: PartialShrinker<Original, H>,
   buildFn: Original.(A, B, C, D, E, F, G, H) -> Original
): Shrinker<Original> = partialShrinkerA.bind(partialShrinkerB).bind(partialShrinkerC)
   .bind(partialShrinkerD).bind(partialShrinkerE).bind(partialShrinkerF)
   .bind(partialShrinkerG).bind(partialShrinkerH)
   .build { (abcdefg, h) ->
      val (abcdef, g) = abcdefg
      val (abcde, f) = abcdef
      val (abcd, e) = abcde
      val (abc, d) = abcd
      val (ab, c) = abc
      val (a, b) = ab
      buildFn(a, b, c, d, e, f, g, h)
   }

fun <Original, A, B, C, D, E, F, G, H, I> ShrinkerBuilder<Original>.build(
   partialShrinkerA: PartialShrinker<Original, A>,
   partialShrinkerB: PartialShrinker<Original, B>,
   partialShrinkerC: PartialShrinker<Original, C>,
   partialShrinkerD: PartialShrinker<Original, D>,
   partialShrinkerE: PartialShrinker<Original, E>,
   partialShrinkerF: PartialShrinker<Original, F>,
   partialShrinkerG: PartialShrinker<Original, G>,
   partialShrinkerH: PartialShrinker<Original, H>,
   partialShrinkerI: PartialShrinker<Original, I>,
   buildFn: Original.(A, B, C, D, E, F, G, H, I) -> Original
): Shrinker<Original> = partialShrinkerA.bind(partialShrinkerB).bind(partialShrinkerC)
   .bind(partialShrinkerD).bind(partialShrinkerE).bind(partialShrinkerF)
   .bind(partialShrinkerG).bind(partialShrinkerH).bind(partialShrinkerI)
   .build { (abcdefgh, i) ->
      val (abcdefg, h) = abcdefgh
      val (abcdef, g) = abcdefg
      val (abcde, f) = abcdef
      val (abcd, e) = abcde
      val (abc, d) = abcd
      val (ab, c) = abc
      val (a, b) = ab
      buildFn(a, b, c, d, e, f, g, h, i)
   }

fun <Original, A, B, C, D, E, F, G, H, I, J> ShrinkerBuilder<Original>.build(
   partialShrinkerA: PartialShrinker<Original, A>,
   partialShrinkerB: PartialShrinker<Original, B>,
   partialShrinkerC: PartialShrinker<Original, C>,
   partialShrinkerD: PartialShrinker<Original, D>,
   partialShrinkerE: PartialShrinker<Original, E>,
   partialShrinkerF: PartialShrinker<Original, F>,
   partialShrinkerG: PartialShrinker<Original, G>,
   partialShrinkerH: PartialShrinker<Original, H>,
   partialShrinkerI: PartialShrinker<Original, I>,
   partialShrinkerJ: PartialShrinker<Original, J>,
   buildFn: Original.(A, B, C, D, E, F, G, H, I, J) -> Original
): Shrinker<Original> = partialShrinkerA
   .bind(partialShrinkerB)
   .bind(partialShrinkerC)
   .bind(partialShrinkerD)
   .bind(partialShrinkerE)
   .bind(partialShrinkerF)
   .bind(partialShrinkerG)
   .bind(partialShrinkerH)
   .bind(partialShrinkerI)
   .bind(partialShrinkerJ)
   .build { (abcdefghi, j) ->
      val (abcdefgh, i) = abcdefghi
      val (abcdefg, h) = abcdefgh
      val (abcdef, g) = abcdefg
      val (abcde, f) = abcdef
      val (abcd, e) = abcde
      val (abc, d) = abcd
      val (ab, c) = abc
      val (a, b) = ab
      buildFn(a, b, c, d, e, f, g, h, i, j)
   }

fun <Original, Value> PartialShrinker<Original, Value>.build(
   shrinkFn: Original.(Value) -> Original
) = object : Shrinker<Original> {
   override fun shrink(
      value: Original
   ): List<Original> {
      return shrinker
         .shrink(selection(value))
         .map { shrinkFn(value, it) }
   }
}

fun <Original, A, B> ShrinkerBuilder<Original>.bind(
   selectionA: PartialShrinker<Original, A>,
   selectionB: PartialShrinker<Original, B>,
) = selectionA.bind(selectionB)

fun <Original, A, B> PartialShrinker<Original, A>.bind(
   other: PartialShrinker<Original, B>
) = PartialShrinker<Original, Pair<A, B>>(
   { this.selection(it) to other.selection(it) },
   FlatmapShrinker(this.shrinker, other.shrinker)
)


class PartialShrinker<Original, V>(
   val selection: (Original) -> V,
   val shrinker: Shrinker<V>
) {
   companion object
}

fun <T> Shrinker<T>.withCache() = when (this) {
   is ShrinkerWithCache -> this
   else -> ShrinkerWithCache(this)
}

open class ShrinkerWithCache<T>(
   private val shrinker: Shrinker<T>
) : Shrinker<T> {
   val shrinks = mutableMapOf<T, List<T>>()
   override fun shrink(
      value: T
   ) = shrinks.getOrPut(
      value
   ) { shrinker.shrink(value) }
}

open class FlatmapShrinker<A, B>(
   shrinkerA: Shrinker<A>,
   shrinkerB: Shrinker<B>,
) : Shrinker<Pair<A, B>> {
   val shrinker1 = shrinkerA.withCache()
   val shrinker2 = shrinkerB.withCache()
   override fun shrink(value: Pair<A, B>): List<Pair<A, B>> {
      val shrinksA = shrinkerA.shrink(value.first)
      val shrinksB = shrinkerB.shrink(value.second)

      if (shrinksA.isEmpty() && shrinksB.isEmpty()) return emptyList()

      val nonEmptyShrinks1 = shrinksA.ifEmpty { listOf(value.first) }
      val nonEmptyShrinks2 = shrinksB.ifEmpty { listOf(value.second) }
      return nonEmptyShrinks1.flatMap { valueA ->
         nonEmptyShrinks2.map { valueB ->
            valueA to valueB
         }
      }
   }
}

class ShrinkerBuilder<Original> {
   fun <T> select(
      lambda: Original.() -> T
   ) = ShrinkerSelectPart(lambda)

   infix fun <T> ShrinkerSelectPart<Original, T>.shrinkWith(
      shrinker: Shrinker<T>
   ) = PartialShrinker(selection, shrinker)
}

fun <Original> createShrinker(
   shrinkerBuilder: ShrinkerBuilder<Original>.() -> Shrinker<Original>
) = ShrinkerBuilder<Original>().shrinkerBuilder()
