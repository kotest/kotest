package io.kotest.equals

import io.kotest.assertions.equals.Equality
import io.kotest.assertions.equals.EqualityResult
import io.kotest.matchers.equality.beEqualToUsingFields
import kotlin.reflect.KProperty

class ReflectionUsingFieldsEquality<T : Any>(
   private val fields: Array<out KProperty<*>>
) : Equality<T> {

   override fun name(): String {
      return "reflection equality using fields ${fields.map { it.name }}"
   }

   override fun verify(actual: T, expected: T): EqualityResult {
      val result = beEqualToUsingFields(expected, *fields).test(actual)
      if (result.passed()) return EqualityResult.equal(actual, expected, this)
      return EqualityResult.notEqual(actual, expected, this).withDetails { result.failureMessage() }
   }

   override fun toString(): String = name()
}

fun <T : Any> Equality.Companion.byReflectionUsingFields(vararg fields: KProperty<*>) =
   ReflectionUsingFieldsEquality<T>(fields)
