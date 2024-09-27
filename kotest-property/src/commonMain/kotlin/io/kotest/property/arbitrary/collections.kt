package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.Shrinker
import kotlin.jvm.JvmOverloads
import kotlin.math.max
import kotlin.random.nextInt

/**
 * Returns an [Arb] whose values are chosen randomly from those in the supplied collection.
 * May not cover all items. If you want an exhaustive selection from the list, see
 * [Exhaustive.collection][io.kotest.property.exhaustive.collection]
 */
fun <T> Arb.Companion.element(collection: Collection<T>): Arb<T> {
   require(collection.isNotEmpty()) { "The supplied collection must not be empty." }
   return arbitrary { collection.random(it.random) }
}

/**
 * Alias for [element]
 */
fun <T> Arb.Companion.of(collection: Collection<T>): Arb<T> = element(collection)

/**
 * Returns an [Arb] whose values are chosen randomly from those in the supplied collection.
 * May not cover all items. If you want an exhaustive selection from the list, see
 * [Exhaustive.collection][io.kotest.property.exhaustive.collection]
 */
fun <T> Arb.Companion.element(vararg collection: T): Arb<T> {
   require(collection.isNotEmpty()) { "The supplied collection must not be empty." }
   return arbitrary { collection.random(it.random) }
}

/**
 * Alias for [element]
 */
fun <T> Arb.Companion.of(vararg collection: T): Arb<T> = element(*collection)

fun <A> Arb.Companion.set(gen: Gen<A>, size: Int, slippage: Int = 10): Arb<Set<A>> = set(gen, size..size, slippage)

/**
 * Returns an [Arb] whose of values are a set of values generated by the given element generator.
 * The size of each set is determined randomly within the specified [range].
 *
 * Note: This may fail to terminate if the element generator cannot produce a large enough number of
 * unique items to satify the required set size
 *
 * @param gen the generator of values to populate each set
 * @param range each set will have a random size within this given range
 * @param slippage when generating values, we may have repeats if the underlying gen is random.
 *        The slippage factor determines how many times we continue after retreiving a duplicate value.
 *        The total acceptable number of misses is the slippage factor multipled by the target set size.
 *        If this value is not specified (null) then the slippage value will be 10.
 */
@JvmOverloads
fun <A> Arb.Companion.set(gen: Gen<A>, range: IntRange = 0..100, slippage: Int = 10): Arb<Set<A>> {
   check(!range.isEmpty())
   check(range.first >= 0)
   return arbitrary(SetShrinker(range)) {
      val genIter = gen.generate(it).iterator()
      val targetSize = it.random.nextInt(range)
      val set = mutableSetOf<A>()
      var iterations = 0
      val maxMisses = targetSize * slippage
      // We may generate duplicates, but we don't know if the underlying gen has sufficient cardinality
      // to satisfy our range, so we can try for a while, but must not try forever.
      // The slippage factor controls how many times we will accept a non unique element before giving up,
      // which is the number of elements in the target set * slippage
      while (iterations < maxMisses && set.size < targetSize && genIter.hasNext()) {
         val size = set.size
         set.add(genIter.next().value)
         if (set.size == size) iterations++
      }
      check(set.size == targetSize) {
         "the target size requirement of $targetSize could not be satisfied after $iterations consecutive samples"
      }
      set
   }
}

/**
 * Returns an [Arb] which returns lists that contain values generated by the given generator.
 * The size of each list is determined randomly within the specified [range].
 *
 * The edge cases are the empty list, if the range includes zero, and a list with a repeated element (taken
 * as the first edge case from the underlying gen).
 *
 * Shrinking is performed by removing elements from the list until the empty list is reached.
 */
@JvmOverloads
fun <A> Arb.Companion.list(gen: Gen<A>, range: IntRange = 0..100): Arb<List<A>> {
   check(!range.isEmpty())
   check(range.first >= 0)

   return arbitrary(
      edgecaseFn = { rs ->
         val emptyList = emptyList<A>()
         val singleList: List<A>? = when (gen) {
            is Arb -> (gen.edgecase(rs) ?: gen.next(rs))?.let { listOf(it) }
            is Exhaustive -> gen.values.firstOrNull()?.let { listOf(it) }
         }
         val repeatedList: List<A>? = when {
            range.last < 2 -> null // too small for repeats
            gen is Arb -> (gen.edgecase(rs) ?: gen.next(rs))?.let { a -> List(max(2, range.first)) { a } }
            gen is Exhaustive -> gen.values.firstOrNull()?.let { a -> List(max(2, range.first)) { a } }
            else -> null
         }
         listOfNotNull(emptyList, singleList, repeatedList).filter { it.size in range }.distinct().random(rs.random)
      },
      shrinker = ListShrinker(range),
      sampleFn = { rs ->
         val targetSize = rs.random.nextInt(range)
         gen.generate(rs).take(targetSize).toList().map { it.value }
      }
   )
}

/**
 * Returns an [Arb] which returns lists that contain values generated by the given generator.
 * The size of each list is determined randomly within the specified [range].
 *
 * The edge cases are the empty list, if the range includes zero, and a list with a repeated element (taken
 * as the first edge case from the underlying gen).
 *
 * Shrinking is performed by removing elements from the list until the empty list is reached.
 */
fun <A> Arb.Companion.list(gen: Gen<A>, range: Arb<IntRange>): Arb<List<A>> {
   return arbitrary {
      val domain = range.bind()
      list(gen, domain).bind()
   }
}

/**
 * Returns an [Arb] whose of values are a list of values generated by the current arb.
 * The size of each list is determined randomly by the specified [size].
 *
 * Shrinking is performed by removing elements from the list until the empty list.
 *
 * This function is a convenience for Arb.list(gen, range)
 *
 * @param size minimum and maximum number of items in the lists produced by the returned [Arb]
 */
fun <A> Arb<A>.chunked(size: IntRange): Arb<List<A>> = Arb.list(this, size)

/**
 * Returns an [Arb] whose of values are a list of values generated by the current arb.
 * The size of each list is determined randomly by the specified [minSize] and [maxSize].
 *
 * Shrinking is performed by removing elements from the list until the empty list.
 *
 * This function is a convenience for Arb.list(gen, range)
 *
 * @param minSize minimum number of items in the lists produced by the returned [Arb]
 * @param maxSize maximum number of items in the lists produced by the returned [Arb]
 */
fun <A> Arb<A>.chunked(minSize: Int, maxSize: Int): Arb<List<A>> = Arb.list(this, minSize..maxSize)

/**
 * A Shrinker for sets, utilizing the ListShrinker.
 */
class SetShrinker<A>(private val range: IntRange) : Shrinker<Set<A>> {
   val listShrinker = ListShrinker<A>(range)

   override fun shrink(value: Set<A>): List<Set<A>> =
      listShrinker.shrink(value.toList())
         .map { it.toSet() }
}

/**
 * A Shrinker for lists. The candidates at each step include:
 *  - the empty list
 *  - the input list with the tail element removed
 *  - the input list with the head element removed
 *  - the first n / 2 elements
 */
class ListShrinker<A>(private val range: IntRange) : Shrinker<List<A>> {
   override fun shrink(value: List<A>): List<List<A>> = when {
      value.isEmpty() -> emptyList()
      value.size == 1 -> if (range.contains(0)) listOf(emptyList()) else emptyList()
      else -> listOf(
         value.take(1), // just the first element
         value.dropLast(1),
         value.take(value.size / 2),
         value.drop(1)
      ).filter { it.size in range }
   }
}
