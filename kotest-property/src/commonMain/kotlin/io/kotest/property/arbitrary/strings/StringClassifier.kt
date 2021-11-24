package io.kotest.property.arbitrary.strings

import io.kotest.property.Classifier

class StringClassifier(private val min: Int, private val max: Int) : Classifier<String> {

   override fun classify(value: String): String? = when {
      value.isEmpty() -> "EMPTY STRING"
      value.isBlank() -> "BLANK STRING"
      value.length == min -> "MIN LENGTH"
      value.length == max -> "MAX LENGTH"
      value.length == 1 && value[0].isDigit() -> "SINGLE CHARACTER DIGIT"
      value.length == 1 && value[0].isLetter() -> "SINGLE CHARACTER LETTER"
      value.all { it.isLetterOrDigit() } -> "ANY LENGTH LETTER OR DIGITS"
      else -> null
   }
}
