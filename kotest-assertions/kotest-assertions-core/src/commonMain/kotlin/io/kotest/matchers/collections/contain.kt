package io.kotest.matchers.collections

import io.kotest.assertions.equals.Equality
import io.kotest.assertions.print.print
import io.kotest.assertions.similarity.possibleMatchesDescription
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

// Primitive array overloads
infix fun BooleanArray.shouldContain(t: Boolean): BooleanArray = apply { asList().shouldContain(t) }
infix fun BooleanArray.shouldNotContain(t: Boolean): BooleanArray = apply { asList().shouldNotContain(t) }
infix fun ByteArray.shouldContain(t: Byte): ByteArray = apply { asList().shouldContain(t) }
infix fun ByteArray.shouldNotContain(t: Byte): ByteArray = apply { asList().shouldNotContain(t) }
infix fun ShortArray.shouldContain(t: Short): ShortArray = apply { asList().shouldContain(t) }
infix fun ShortArray.shouldNotContain(t: Short): ShortArray = apply { asList().shouldNotContain(t) }
infix fun CharArray.shouldContain(t: Char): CharArray = apply { asList().shouldContain(t) }
infix fun CharArray.shouldNotContain(t: Char): CharArray = apply { asList().shouldNotContain(t) }
infix fun IntArray.shouldContain(t: Int): IntArray = apply { asList().shouldContain(t) }
infix fun IntArray.shouldNotContain(t: Int): IntArray = apply { asList().shouldNotContain(t) }
infix fun LongArray.shouldContain(t: Long): LongArray = apply { asList().shouldContain(t) }
infix fun LongArray.shouldNotContain(t: Long): LongArray = apply { asList().shouldNotContain(t) }
infix fun FloatArray.shouldContain(t: Float): FloatArray = apply { asList().shouldContain(t) }
infix fun FloatArray.shouldNotContain(t: Float): FloatArray = apply { asList().shouldNotContain(t) }
infix fun DoubleArray.shouldContain(t: Double): DoubleArray = apply { asList().shouldContain(t) }
infix fun DoubleArray.shouldNotContain(t: Double): DoubleArray = apply { asList().shouldNotContain(t) }

// Infix
infix fun <T, I : Iterable<T>> I.shouldNotContain(t: T): I = shouldNotContain(t, Equality.default())
infix fun <T> Array<T>.shouldNotContain(t: T): Array<T> = shouldNotContain(t, Equality.default())
infix fun <T, I : Iterable<T>> I.shouldContain(t: T): I = shouldContain(t, Equality.default())
infix fun <T> Array<T>.shouldContain(t: T): Array<T> = shouldContain(t, Equality.default())

// Should not
fun <T, I : Iterable<T>> I.shouldNotContain(t: T, comparator: Equality<T>): I = apply {
   toList() shouldNot contain(t, comparator)
}

fun <T> Array<T>.shouldNotContain(t: T, comparator: Equality<T>): Array<T> = apply {
   asList().shouldNotContain(t, comparator)
}

// Should
fun <T, I : Iterable<T>> I.shouldContain(t: T, comparator: Equality<T>): I = apply {
   toList() should contain(t, comparator)
}

fun <T> Array<T>.shouldContain(t: T, comparator: Equality<T>): Array<T> = apply {
   asList().shouldContain(t, comparator)
}

// Matcher
fun <T, C : Collection<T>> contain(t: T, verifier: Equality<T> = Equality.default()) = object : Matcher<C> {
   override fun test(value: C) : MatcherResult {
      val passedAtIndexes = value.mapIndexedNotNull {
         index, it -> if(verifier.verify(it, t).areEqual()) index else null
      }
      val passed = passedAtIndexes.isNotEmpty()
      val possibleMatches = {
         if (!passed && (verifier.name() == Equality.default<T>().name())) {
            val candidates = possibleMatchesDescription(value.toSet(), t)
            if (candidates.isEmpty()) "" else "\nPossibleMatches:$candidates"
         } else ""
      }
      return MatcherResult(
         passed,
         {
            "Collection should contain element ${t.print().value} based on ${verifier.name()}; " +
               "but the collection is ${value.print().value}${possibleMatches()}"
         },
         { "Collection should not contain element ${t.print().value} based on ${verifier.name()}, but it did at index(es):${passedAtIndexes.print().value}" }
      )
   }
}
