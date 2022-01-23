package io.kotest.equals.types

import io.kotest.equals.EqualityResult
import io.kotest.equals.EqualityVerifier
import io.kotest.equals.EqualityVerifiers
import io.kotest.equals.areNotEqual
import kotlin.math.exp

open class RegexEqualityVerifier : EqualityVerifier<Regex> {
   override fun name(): String = "regex pattern equality"

   override fun areEqual(actual: Regex, expected: Regex): EqualityResult {
      if (actual.pattern == expected.pattern) {
         return EqualityResult.equal(actual, expected, this)
      }

      return EqualityResult.notEqual(actual, expected, this)
   }
}

fun EqualityVerifiers.regexEquality(): RegexEqualityVerifier = RegexEqualityVerifier()
