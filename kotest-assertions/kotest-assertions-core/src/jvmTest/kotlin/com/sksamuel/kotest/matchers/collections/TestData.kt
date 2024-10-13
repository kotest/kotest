package com.sksamuel.kotest.matchers.collections

data class Fruit(
   val name: String,
   val color: String,
   val taste: String
)

val sweetGreenApple = Fruit("apple", "green", "sweet")
val sweetRedApple = Fruit("apple", "red", "sweet")
val sweetGreenPear = Fruit("pear", "green", "sweet")
val sourYellowLemon = Fruit("lemon", "yellow", "sour")
val tartRedCherry = Fruit("cherry", "red", "tart")
val bitterPurplePlum = Fruit("plum", "purple", "bitter")

/**
 * An infinite stream of '1L'.
 */
class InfiniteIterable : Iterable<Long> {
   override fun iterator(): Iterator<Long> = object : Iterator<Long> {
      override fun hasNext(): Boolean = true

      override fun next(): Long = 1
   }
}

/**
 * This class implements `equals` and `hashCode` by [equalsDelegate] and Comparable by [comparableDelegate].
 */
class ConfigurableEquality(
   val equalsDelegate: String,
   val comparableDelegate: String,
) : Comparable<ConfigurableEquality> {

   override fun compareTo(other: ConfigurableEquality): Int = comparableDelegate.compareTo(other.comparableDelegate)

   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is ConfigurableEquality) return false

      if (equalsDelegate != other.equalsDelegate) return false

      return true
   }

   override fun hashCode(): Int {
      return equalsDelegate.hashCode()
   }

   override fun toString(): String = "($equalsDelegate, $comparableDelegate)"
}

internal class NonUniqueSet : Set<Int> {
   private val elements = listOf(1, 1)

   override val size: Int = elements.size

   override fun contains(element: Int): Boolean = elements.contains(element)

   override fun containsAll(elements: Collection<Int>): Boolean = elements.containsAll(elements)

   override fun isEmpty(): Boolean = false

   override fun iterator(): Iterator<Int> = elements.iterator()
}
