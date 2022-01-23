package io.kotest.comparators

import io.kotest.equals.EqualityVerifier
import io.kotest.equals.EqualityVerifiers
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.equality.beEqualToUsingFields
import kotlin.reflect.KProperty

class ReflectionUsingFieldsComparator<T : Any>(
   private val fields: Array<out KProperty<*>>
) : EqualityVerifier<T> {
   override fun name(): String {
      return "reflection comparison using fields $fields"
   }

   override fun areEqual(actual: T, expected: T): MatcherResult =
      beEqualToUsingFields(expected, *fields).test(actual)

   override fun toString(): String = name()
}

fun <T : Any> EqualityVerifiers.reflectionUsingFields(vararg fields: KProperty<*>) =
   ReflectionUsingFieldsComparator<T>(fields)
