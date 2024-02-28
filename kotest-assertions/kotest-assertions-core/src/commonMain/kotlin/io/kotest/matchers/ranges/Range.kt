package io.kotest.matchers.ranges

internal data class Range<T: Comparable<T>>(
    val start: RangeEdge<T>,
    val end: RangeEdge<T>
) {
   init {
      require(start.value <= end.value) {
         "${start.value} cannot be after ${end.value}"
      }
   }

   override fun toString(): String {
      return "${if(start.edgeType == RangeEdgeType.INCLUSIVE) "[" else "("}${start.value}, ${end.value}${if(end.edgeType == RangeEdgeType.INCLUSIVE) "]" else ")"}"
   }

   fun isEmpty() = start.value == end.value && (
         start.edgeType == RangeEdgeType.EXCLUSIVE ||
         end.edgeType == RangeEdgeType.EXCLUSIVE
      )

   fun intersect(other: Range<T>): Boolean = !this.lessThan(other) && !other.lessThan(this)

   fun lessThan(other: Range<T>): Boolean {
      val endOfThis: T = this.end.value
      val startOfOther: T = other.start.value
      return when {
         (this.end.edgeType== RangeEdgeType.INCLUSIVE && other.start.edgeType == RangeEdgeType.INCLUSIVE) -> (endOfThis < startOfOther)
         else -> (endOfThis <= startOfOther)
      }
   }

   fun greaterThan(other: Range<T>) = other.lessThan(this)

   fun contains(other: Range<T>) = contains(other.start) && contains(other.end)

   fun contains(edge: RangeEdge<T>) = when {
      edge.value < this.start.value -> false
      edge.value == this.start.value ->
         this.start.edgeType == RangeEdgeType.INCLUSIVE || edge.edgeType == RangeEdgeType.EXCLUSIVE
      this.start.value < edge.value && edge.value < this.end.value -> true
      edge.value == this.end.value ->
         this.end.edgeType == RangeEdgeType.INCLUSIVE || edge.edgeType == RangeEdgeType.EXCLUSIVE
      else -> false
   }

   companion object {
      fun<T: Comparable<T>> ofClosedRange(range: ClosedRange<T>) = Range(
          start = RangeEdge(range.start, RangeEdgeType.INCLUSIVE),
          end = RangeEdge(range.endInclusive, RangeEdgeType.INCLUSIVE)
      )

      @OptIn(ExperimentalStdlibApi::class)
      fun<T: Comparable<T>> ofOpenEndRange(range: OpenEndRange<T>) = Range(
          start = RangeEdge(range.start, RangeEdgeType.INCLUSIVE),
          end = RangeEdge(range.endExclusive, RangeEdgeType.EXCLUSIVE)
      )

      fun<T: Comparable<T>> openOpen(start: T, end: T) = Range(
          start = RangeEdge(start, RangeEdgeType.EXCLUSIVE),
          end = RangeEdge(end, RangeEdgeType.EXCLUSIVE)
      )

      fun<T: Comparable<T>> openClosed(start: T, end: T) = Range(
          start = RangeEdge(start, RangeEdgeType.EXCLUSIVE),
          end = RangeEdge(end, RangeEdgeType.INCLUSIVE)
      )

      fun<T: Comparable<T>> closedOpen(start: T, end: T) = Range(
          start = RangeEdge(start, RangeEdgeType.INCLUSIVE),
          end = RangeEdge(end, RangeEdgeType.EXCLUSIVE)
      )

      fun<T: Comparable<T>> closedClosed(start: T, end: T) = Range(
          start = RangeEdge(start, RangeEdgeType.INCLUSIVE),
          end = RangeEdge(end, RangeEdgeType.INCLUSIVE)
      )
   }
}

internal fun<T: Comparable<T>> ClosedRange<T>.toClosedClosedRange(): Range<T> = Range(
   start = RangeEdge(this.start, RangeEdgeType.INCLUSIVE),
   end = RangeEdge(this.endInclusive, RangeEdgeType.INCLUSIVE)
)

@OptIn(ExperimentalStdlibApi::class)
internal fun<T: Comparable<T>> OpenEndRange<T>.toClosedOpenRange(): Range<T> = Range(
   start = RangeEdge(this.start, RangeEdgeType.INCLUSIVE),
   end = RangeEdge(this.endExclusive, RangeEdgeType.EXCLUSIVE)
)

internal enum class RangeEdgeType { INCLUSIVE, EXCLUSIVE }

internal data class RangeEdge<T: Comparable<T>>(val value: T, val edgeType: RangeEdgeType)
