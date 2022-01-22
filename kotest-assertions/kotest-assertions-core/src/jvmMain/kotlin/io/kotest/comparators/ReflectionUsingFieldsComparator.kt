package io.kotest.comparators

import io.kotest.matchers.MatcherResult
import io.kotest.matchers.equality.beEqualToUsingFields
import kotlin.reflect.KProperty

class ReflectionUsingFieldsComparator<T : Any>(
   private val fields: Array<out KProperty<*>>
) : Comparator<T> {
   override fun name(): String {
      return "reflection comparison using fields $fields"
   }

   override fun matches(actual: T, expected: T): MatcherResult =
      beEqualToUsingFields(expected, *fields).test(actual)
}

fun <T : Any> Comparators.reflectionUsingFields(vararg fields: KProperty<*>) =
   ReflectionUsingFieldsComparator<T>(fields)
