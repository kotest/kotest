package io.kotest.property.arbitrary.numbers

import io.kotest.property.Classifier

class IntClassifier(private val min: Int, private val max: Int) : Classifier<Int> {

   constructor(range: IntRange) : this(range.first, range.last)

   override fun classify(value: Int): String? = when {
      value == 0 -> "ZERO"
      value == min -> "MIN"
      value == max -> "MAX"
      value > 0 && value % 2 == 0 -> "POSITIVE EVEN"
      value > 0 -> "POSITIVE ODD"
      value < 0 && value % 2 == 0 -> "NEGATIVE EVEN"
      value < 0 -> "NEGATIVE ODD"
      else -> null
   }
}

class LongClassifier(private val min: Long, private val max: Long) : Classifier<Long> {

   constructor(range: LongRange) : this(range.first, range.last)

   override fun classify(value: Long): String? = when {
      value == 0L -> "ZERO"
      value == min -> "MIN"
      value == max -> "MAX"
      value > 0 && value % 2 == 0L -> "POSITIVE EVEN"
      value > 0 -> "POSITIVE ODD"
      value < 0 && value % 2 == 0L -> "NEGATIVE EVEN"
      value < 0 -> "NEGATIVE ODD"
      else -> null
   }
}


