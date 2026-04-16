package io.kotest.matchers.collections

import io.kotest.assertions.equals.Equality
import io.kotest.assertions.print.print
import io.kotest.assertions.similarity.possibleMatchesDescription
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun BooleanArray.shouldContainExactCopies(t: Boolean, copies: Int): BooleanArray = apply { asList().shouldContainExactCopies(t, copies) }
fun BooleanArray.shouldNotContainExactCopies(t: Boolean, copies: Int): BooleanArray = apply { asList().shouldNotContainExactCopies(t, copies) }
fun ByteArray.shouldContainExactCopies(t: Byte, copies: Int): ByteArray = apply { asList().shouldContainExactCopies(t, copies) }
fun ByteArray.shouldNotContainExactCopies(t: Byte, copies: Int): ByteArray = apply { asList().shouldNotContainExactCopies(t, copies) }
fun ShortArray.shouldContainExactCopies(t: Short, copies: Int): ShortArray = apply { asList().shouldContainExactCopies(t, copies) }
fun ShortArray.shouldNotContainExactCopies(t: Short, copies: Int): ShortArray = apply { asList().shouldNotContainExactCopies(t, copies) }
fun CharArray.shouldContainExactCopies(t: Char, copies: Int): CharArray = apply { asList().shouldContainExactCopies(t, copies) }
fun CharArray.shouldNotContainExactCopies(t: Char, copies: Int): CharArray = apply { asList().shouldNotContainExactCopies(t, copies) }
fun IntArray.shouldContainExactCopies(t: Int, copies: Int): IntArray = apply { asList().shouldContainExactCopies(t, copies) }
fun IntArray.shouldNotContainExactCopies(t: Int, copies: Int): IntArray = apply { asList().shouldNotContainExactCopies(t, copies) }
fun LongArray.shouldContainExactCopies(t: Long, copies: Int): LongArray = apply { asList().shouldContainExactCopies(t, copies) }
fun LongArray.shouldNotContainExactCopies(t: Long, copies: Int): LongArray = apply { asList().shouldNotContainExactCopies(t, copies) }
fun FloatArray.shouldContainExactCopies(t: Float, copies: Int): FloatArray = apply { asList().shouldContainExactCopies(t, copies) }
fun FloatArray.shouldNotContainExactCopies(t: Float, copies: Int): FloatArray = apply { asList().shouldNotContainExactCopies(t, copies) }
fun DoubleArray.shouldContainExactCopies(t: Double, copies: Int): DoubleArray = apply { asList().shouldContainExactCopies(t, copies) }
fun DoubleArray.shouldNotContainExactCopies(t: Double, copies: Int): DoubleArray = apply { asList().shouldNotContainExactCopies(t, copies) }

fun <T, I : Iterable<T>> I.shouldNotContainExactCopies(t: T, copies: Int): I = apply {
   toList() shouldNot containExactCopies(t, copies)
}

fun <T> Array<T>.shouldNotContainExactCopies(t: T, copies: Int): Array<T> = apply {
   asList().shouldNotContainExactCopies(t, copies)
}

// Should
fun <T, I : Iterable<T>> I.shouldContainExactCopies(t: T, copies: Int): I = apply {
   toList() should containExactCopies(t, copies)
}

fun <T> Array<T>.shouldContainExactCopies(t: T, copies: Int): Array<T> = apply {
   asList().shouldContainExactCopies(t, copies)
}
fun <T, C : Collection<T>> containExactCopies(t: T, copies: Int) = object : Matcher<C> {
   override fun test(value: C) : MatcherResult {
      require(copies > 0) { "Copies should be positive, was $copies" }
      val passedAtIndexes = value.mapIndexedNotNull {
            index, it -> if(it == t) index else null
      }
      val passed = passedAtIndexes.size == copies
      val possibleMatches = {
         if (!passed) {
            val candidates = possibleMatchesDescription(value.toSet(), t)
            if (candidates.isEmpty()) "" else "\nPossibleMatches:$candidates"
         } else ""
      }
      return MatcherResult(
         passed,
         {
            "Collection should contain $copies copies of element ${t.print().value}; " +
               "but contained ${passedAtIndexes.size} copies ${if(passedAtIndexes.size > 0) "at index(es) ${passedAtIndexes.print().value}, and " else "but "}" +
               "the collection is ${value.print().value}${possibleMatches()}"
         },
         { "Collection should not contain $copies copies of element ${t.print().value}, but it did at index(es):${passedAtIndexes.print().value}" }
      )
   }
}
