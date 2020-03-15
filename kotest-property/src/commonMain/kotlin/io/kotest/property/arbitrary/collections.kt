package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.Shrinker
import kotlin.jvm.JvmOverloads
import kotlin.random.nextInt

/**
 * Returns an [Arb] whose values are chosen randomly from those in the supplied collection.
 * May not cover all items. If you want an exhaustive selection from the list, see [Exhaustive.collection]
 */
fun <T> Arb.Companion.element(collection: Collection<T>): Arb<T> = Arb.create { collection.random(it.random) }

/**
 * Returns an [Arb] whose values are chosen randomly from those in the supplied collection.
 * May not cover all items. If you want an exhaustive selection from the list, see [Exhaustive.collection]
 */
fun <T> Arb.Companion.element(vararg collection: T): Arb<T> = Arb.create { collection.random(it.random) }

/**
 * Returns an [Arb] whose of values are a set of values generated by the given element generator.
 * The size of each set is determined randomly within the specified [range].
 *
 * Note: This may fail to terminate if the element generator cannot produce a large enough number of
 * unique items to satify the required set size
 */
@JvmOverloads
fun <A> Arb.Companion.set(gen: Gen<A>, range: IntRange = 0..100): Arb<Set<A>> {
   check(!range.isEmpty())
   check(range.first >= 0)
   return arb {
      sequence {
         val genIter = gen.generate(it).iterator()
         val targetSize = it.random.nextInt(range)
         val set = mutableSetOf<A>()
         while (set.size < targetSize && genIter.hasNext()) {
            set.add(genIter.next().value)
         }
         check(set.size == targetSize)
         yield(set)
      }
   }
}

/**
 * Returns an [Arb] whose of values are a list of values generated by the given generator.
 * The size of each list is determined randomly by the specified [range].
 *
 * Shrinking is performed by removing elements from the list until the empty list.
 */
@JvmOverloads
fun <A> Arb.Companion.list(gen: Gen<A>, range: IntRange = 0..100): Arb<List<A>> {
   check(!range.isEmpty())
   check(range.first >= 0)
   return arb {
      sequence {
         val genIter = gen.generate(it).iterator()
         while (true) {
            val targetSize = it.random.nextInt(range)
            val list = ArrayList<A>(targetSize)
            while (list.size < targetSize && genIter.hasNext()) {
               list.add(genIter.next().value)
            }
            check(list.size == targetSize)
            yield(list)
         }
      }
   }
}

/**
 * Returns an [Arb] whose of values are a list of values generated by the current arb.
 * The size of each list is determined randomly by the specified [size].
 *
 * Shrinking is performed by removing elements from the list until the empty list.
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
 * @param minSize minimum number of items in the lists produced by the returned [Arb]
 * @param maxSize maximum number of items in the lists produced by the returned [Arb]
 */
fun <A> Arb<A>.chunked(minSize: Int, maxSize: Int): Arb<List<A>> = Arb.list(this, minSize..maxSize)

class ListShrinker<A> : Shrinker<List<A>> {
   override fun shrink(value: List<A>): List<List<A>> {
      return if (value.isEmpty()) emptyList() else {
         listOf(
            emptyList(),
            value.takeLast(1),
            value.take(value.size / 3),
            value.take(value.size / 2),
            value.take(value.size * 2 / 3),
            value.dropLast(1)
         )
      }
   }
}
