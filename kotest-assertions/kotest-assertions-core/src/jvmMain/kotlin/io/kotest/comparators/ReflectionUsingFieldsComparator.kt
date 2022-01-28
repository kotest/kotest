package io.kotest.comparators

import io.kotest.equals.EqualityResult
import io.kotest.equals.EqualityVerifier
import io.kotest.equals.EqualityVerifiers
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.equality.beEqualToUsingFields
import kotlin.reflect.KProperty

class ReflectionUsingFieldsComparator<T : Any>(
   private val fields: Array<out KProperty<*>>
) : EqualityVerifier<T> {
   override fun name(): String {
      return "reflection comparison using fields ${fields.map { it.name }}"
   }

   override fun verify(actual: T, expected: T): EqualityResult {
      val result = beEqualToUsingFields(expected, *fields).test(actual)
      if (result.passed()) return EqualityResult.equal(actual, expected, this)
      return EqualityResult.notEqual(actual, expected, this).withDetails { result.failureMessage() }
   }

   override fun toString(): String = name()
}

fun <T : Any> EqualityVerifiers.reflectionUsingFields(vararg fields: KProperty<*>) =
   ReflectionUsingFieldsComparator<T>(fields)
