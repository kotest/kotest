package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.assertions.submatching.PartialCollectionMatch
import io.kotest.assertions.submatching.findPartialMatches
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <T> Iterable<T>.shouldStartWith(element: T) = toList().shouldStartWith(listOf(element))
infix fun <T> Iterable<T>.shouldStartWith(slice: Iterable<T>) = toList().shouldStartWith(slice.toList())
infix fun <T> Iterable<T>.shouldStartWith(slice: Array<T>) = toList().shouldStartWith(slice.asList())

infix fun <T> Array<T>.shouldStartWith(element: T) = asList().shouldStartWith(listOf(element))
infix fun <T> Array<T>.shouldStartWith(slice: Collection<T>) = asList().shouldStartWith(slice)
infix fun <T> Array<T>.shouldStartWith(slice: Array<T>) = asList().shouldStartWith(slice.asList())

infix fun <T> List<T>.shouldStartWith(element: T) = this should startWith(listOf(element))
infix fun <T> List<T>.shouldStartWith(slice: Collection<T>) = this should startWith(slice)

infix fun <T> Iterable<T>.shouldNotStartWith(element: T) = toList().shouldNotStartWith(listOf(element))
infix fun <T> Iterable<T>.shouldNotStartWith(slice: Iterable<T>) = toList().shouldNotStartWith(slice.toList())
infix fun <T> Iterable<T>.shouldNotStartWith(slice: Array<T>) = toList().shouldNotStartWith(slice.asList())

infix fun <T> Array<T>.shouldNotStartWith(element: T) = asList().shouldNotStartWith(listOf(element))
infix fun <T> Array<T>.shouldNotStartWith(slice: Collection<T>) = asList().shouldNotStartWith(slice)
infix fun <T> Array<T>.shouldNotStartWith(slice: Array<T>) = asList().shouldNotStartWith(slice.asList())

infix fun <T> List<T>.shouldNotStartWith(element: T) = this shouldNot startWith(listOf(element))
infix fun <T> List<T>.shouldNotStartWith(slice: Collection<T>) = this shouldNot startWith(slice)

fun <T> startWith(expectedSlice: Collection<T>) = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      val comparison = SliceComparison.of(expectedSlice.toList(), value, SliceComparison.Companion.SliceType.START)

      val partialMatchesDescription = { describePartialMatchesInCollection(expectedSlice, value) }
      return MatcherResult(
         comparison.match,
         { "List should start with ${expectedSlice.print().value} but was ${comparison.valueSlice.print().value}\n${comparison.mismatchDescription}${partialMatchesDescription()}" },
         { "List should not start with ${expectedSlice.print().value}" }
      )
   }
}

internal fun<T> describePartialMatchesInCollection(expectedSlice: Collection<T>, value: List<T>): PartialMatchesInCollectionDescription {
   val minLength = maxOf(expectedSlice.size / 3, 2)
   val partialMatches = findPartialMatches(expectedSlice.toList(), value, minLength = minLength)
   val partialMatchesList = partialMatches.withIndex().joinToString("\n") { indexedValue ->
      "Slice[${indexedValue.index}] of expected with indexes: ${indexedValue.value.rangeOfExpected} matched a slice of actual values with indexes: ${indexedValue.value.rangeOfValue}"
   }
   val partialMatchesDescription = value.mapIndexedNotNull { index, element ->
      val indexInMatches = partialMatches.withIndex().filter { match -> match.value.indexIsInValue(index) }
      indexInMatches.takeIf { indexInMatches.isNotEmpty() }?.let {
         val slicesList = when (indexInMatches.size) {
            1 -> " ${indexInMatches.first().index}"
            else -> "s: ${indexInMatches.map { it.index }}"
         }
         "[$index] ${element.print().value} => slice$slicesList"
      }
   }.joinToString("\n")
   val indexesOfUnmatchedElements = expectedSlice.indices.filter { index ->
      partialMatches.none { partialMatch -> index in partialMatch.rangeOfExpected } }
   val expectedSliceAsList = expectedSlice.toList()
   val unmatchedElementsDescription = buildString {
      append(
         indexesOfUnmatchedElements.mapNotNull { index ->
            val element = expectedSliceAsList[index]
            val foundAtIndexes = value.withIndex().filter { it.value == element }.map { it.index }
            if (foundAtIndexes.isEmpty())
               null
            else
               "[$index] ${element.print().value} => Found At Index(es): ${foundAtIndexes.print().value}"
         }.joinToString("\n")
      )
      appendPossibleMatches(
         missing = indexesOfUnmatchedElements.map { expectedSliceAsList[it] },
         expected = value,
      )
   }
   return PartialMatchesInCollectionDescription(
      partialMatchesList,
      partialMatchesDescription,
      unmatchedElementsDescription,
      partialMatches,
      indexesOfUnmatchedElements,
      )
}

internal data class PartialMatchesInCollectionDescription(
   val partialMatchesList: String,
   val partialMatchesDescription: String,
   val unmatchedElementsDescription: String,
   val partialMatches: List<PartialCollectionMatch>,
   val indexesOfUnmatchedElements: List<Int>,
) {
   override fun toString(): String = prefixIfNotEmpty(
      listOf(partialMatchesList,
         partialMatchesDescription,
         prefixIfNotEmpty(unmatchedElementsDescription, "\nElement(s) not in matched slice(s):\n")
      )
         .filter { it.isNotEmpty() }
         .joinToString("\n"),
      "\n"
   )
}

private data class SliceComparison<T>(
   val match: Boolean,
   val mismatchDescription: String,
   val valueSlice: List<T>
) {
   companion object {
      fun <T> of(expectedSlice: List<T>, value: List<T>, sliceType: SliceType): SliceComparison<T> {
         val valueSlice = when (sliceType) {
            SliceType.START -> value.take(expectedSlice.size)
            SliceType.END -> value.takeLast(expectedSlice.size)
         }
         val indexOffset = when (sliceType) {
            SliceType.START -> 0
            SliceType.END -> value.size - expectedSlice.size
         }
         return when {
            (valueSlice == expectedSlice) -> SliceComparison(true, "", valueSlice)
            (valueSlice.size != expectedSlice.size) -> SliceComparison(
               false,
               "Actual collection is shorter than expected slice",
               valueSlice
            )
            else -> SliceComparison(
               false,
               "The following elements failed:\n" +
                  valueSlice.mapIndexedNotNull { index: Int, t: T ->
                     if (t != expectedSlice[index]) "  [${index + indexOffset}] ${valueSlice[index].print().value} => expected: <${expectedSlice[index].print().value}>, but was: <${valueSlice[index].print().value}>"
                     else null
                  }.joinToString("\n"),
               valueSlice
            )
         }
      }

      internal enum class SliceType { START, END }
   }
}

infix fun <T> Iterable<T>.shouldEndWith(element: T) = toList().shouldEndWith(listOf(element))
infix fun <T> Iterable<T>.shouldEndWith(slice: Iterable<T>) = toList().shouldEndWith(slice.toList())
infix fun <T> Iterable<T>.shouldEndWith(slice: Array<T>) = toList().shouldEndWith(slice.asList())

infix fun <T> Array<T>.shouldEndWith(element: T) = asList().shouldEndWith(listOf(element))
infix fun <T> Array<T>.shouldEndWith(slice: Collection<T>) = asList().shouldEndWith(slice)
infix fun <T> Array<T>.shouldEndWith(slice: Array<T>) = asList().shouldEndWith(slice.asList())

infix fun <T> List<T>.shouldEndWith(element: T) = this.shouldEndWith(listOf(element))
infix fun <T> List<T>.shouldEndWith(slice: Collection<T>) = this should endWith(slice)
infix fun <T> List<T>.shouldEndWith(slice: Array<T>) = this.shouldEndWith(slice.toList())

infix fun <T> Iterable<T>.shouldNotEndWith(element: T) = toList().shouldNotEndWith(listOf(element))
infix fun <T> Iterable<T>.shouldNotEndWith(slice: Iterable<T>) = toList().shouldNotEndWith(slice.toList())
infix fun <T> Iterable<T>.shouldNotEndWith(slice: Array<T>) = toList().shouldNotEndWith(slice.asList())

infix fun <T> Array<T>.shouldNotEndWith(element: T) = asList().shouldNotEndWith(listOf(element))
infix fun <T> Array<T>.shouldNotEndWith(slice: Collection<T>) = asList().shouldNotEndWith(slice)
infix fun <T> Array<T>.shouldNotEndWith(slice: Array<T>) = asList().shouldNotEndWith(slice.asList())

infix fun <T> List<T>.shouldNotEndWith(element: T) = this shouldNot endWith(listOf(element))
infix fun <T> List<T>.shouldNotEndWith(slice: Collection<T>) = this shouldNot endWith(slice)

fun <T> endWith(expectedSlice: Collection<T>) = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      val comparison = SliceComparison.of(expectedSlice.toList(), value, SliceComparison.Companion.SliceType.END)

      val partialMatchesDescription by lazy { describePartialMatchesInCollection(expectedSlice, value) }

      return MatcherResult(
         comparison.match,
         { "List should end with ${expectedSlice.print().value} but was ${comparison.valueSlice.print().value}\n${comparison.mismatchDescription}$partialMatchesDescription" },
         { "List should not end with ${expectedSlice.print().value}" }
      )
   }
}

// Primitive array overloads for shouldStartWith / shouldNotStartWith

infix fun BooleanArray.shouldStartWith(slice: BooleanArray): BooleanArray = apply { asList().shouldStartWith(slice.asList()) }
infix fun BooleanArray.shouldNotStartWith(slice: BooleanArray): BooleanArray = apply { asList().shouldNotStartWith(slice.asList()) }
infix fun BooleanArray.shouldEndWith(slice: BooleanArray): BooleanArray = apply { asList().shouldEndWith(slice.asList()) }
infix fun BooleanArray.shouldNotEndWith(slice: BooleanArray): BooleanArray = apply { asList().shouldNotEndWith(slice.asList()) }

infix fun ByteArray.shouldStartWith(slice: ByteArray): ByteArray = apply { asList().shouldStartWith(slice.asList()) }
infix fun ByteArray.shouldNotStartWith(slice: ByteArray): ByteArray = apply { asList().shouldNotStartWith(slice.asList()) }
infix fun ByteArray.shouldEndWith(slice: ByteArray): ByteArray = apply { asList().shouldEndWith(slice.asList()) }
infix fun ByteArray.shouldNotEndWith(slice: ByteArray): ByteArray = apply { asList().shouldNotEndWith(slice.asList()) }

infix fun ShortArray.shouldStartWith(slice: ShortArray): ShortArray = apply { asList().shouldStartWith(slice.asList()) }
infix fun ShortArray.shouldNotStartWith(slice: ShortArray): ShortArray = apply { asList().shouldNotStartWith(slice.asList()) }
infix fun ShortArray.shouldEndWith(slice: ShortArray): ShortArray = apply { asList().shouldEndWith(slice.asList()) }
infix fun ShortArray.shouldNotEndWith(slice: ShortArray): ShortArray = apply { asList().shouldNotEndWith(slice.asList()) }

infix fun CharArray.shouldStartWith(slice: CharArray): CharArray = apply { asList().shouldStartWith(slice.asList()) }
infix fun CharArray.shouldNotStartWith(slice: CharArray): CharArray = apply { asList().shouldNotStartWith(slice.asList()) }
infix fun CharArray.shouldEndWith(slice: CharArray): CharArray = apply { asList().shouldEndWith(slice.asList()) }
infix fun CharArray.shouldNotEndWith(slice: CharArray): CharArray = apply { asList().shouldNotEndWith(slice.asList()) }

infix fun IntArray.shouldStartWith(slice: IntArray): IntArray = apply { asList().shouldStartWith(slice.asList()) }
infix fun IntArray.shouldNotStartWith(slice: IntArray): IntArray = apply { asList().shouldNotStartWith(slice.asList()) }
infix fun IntArray.shouldEndWith(slice: IntArray): IntArray = apply { asList().shouldEndWith(slice.asList()) }
infix fun IntArray.shouldNotEndWith(slice: IntArray): IntArray = apply { asList().shouldNotEndWith(slice.asList()) }

infix fun LongArray.shouldStartWith(slice: LongArray): LongArray = apply { asList().shouldStartWith(slice.asList()) }
infix fun LongArray.shouldNotStartWith(slice: LongArray): LongArray = apply { asList().shouldNotStartWith(slice.asList()) }
infix fun LongArray.shouldEndWith(slice: LongArray): LongArray = apply { asList().shouldEndWith(slice.asList()) }
infix fun LongArray.shouldNotEndWith(slice: LongArray): LongArray = apply { asList().shouldNotEndWith(slice.asList()) }

infix fun FloatArray.shouldStartWith(slice: FloatArray): FloatArray = apply { asList().shouldStartWith(slice.asList()) }
infix fun FloatArray.shouldNotStartWith(slice: FloatArray): FloatArray = apply { asList().shouldNotStartWith(slice.asList()) }
infix fun FloatArray.shouldEndWith(slice: FloatArray): FloatArray = apply { asList().shouldEndWith(slice.asList()) }
infix fun FloatArray.shouldNotEndWith(slice: FloatArray): FloatArray = apply { asList().shouldNotEndWith(slice.asList()) }

infix fun DoubleArray.shouldStartWith(slice: DoubleArray): DoubleArray = apply { asList().shouldStartWith(slice.asList()) }
infix fun DoubleArray.shouldNotStartWith(slice: DoubleArray): DoubleArray = apply { asList().shouldNotStartWith(slice.asList()) }
infix fun DoubleArray.shouldEndWith(slice: DoubleArray): DoubleArray = apply { asList().shouldEndWith(slice.asList()) }
infix fun DoubleArray.shouldNotEndWith(slice: DoubleArray): DoubleArray = apply { asList().shouldNotEndWith(slice.asList()) }

