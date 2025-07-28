package io.kotest.equals

import io.kotest.assertions.equals.Equality
import io.kotest.assertions.equals.EqualityResult
import io.kotest.matchers.equality.beEqualToIgnoringFields
import kotlin.reflect.KProperty

class ReflectionIgnoringFieldsEquality<T : Any>(
   private val property: KProperty<*>,
   private val others: Array<out KProperty<*>>,
   private val ignorePrivateFields: Boolean = true,
) : Equality<T> {
   
   override fun name(): String {
      val plural = if (others.isNotEmpty()) "s" else ""
      val ignoringPrivate = if (ignorePrivateFields) "ignoring" else "including"
      return "reflection equality ignoring field$plural ${(listOf(property) + others).map { it.name }} and $ignoringPrivate private fields"
   }

   fun includingPrivateFields(): ReflectionIgnoringFieldsEquality<T> {
      return withIgnorePrivateFields(false)
   }

   fun ignoringPrivateFields(): ReflectionIgnoringFieldsEquality<T> {
      return withIgnorePrivateFields(true)
   }

   private fun withIgnorePrivateFields(value: Boolean): ReflectionIgnoringFieldsEquality<T> {
      return ReflectionIgnoringFieldsEquality(
         property = property,
         others = others,
         ignorePrivateFields = value,
      )
   }

   override fun verify(actual: T, expected: T): EqualityResult {
      val result = beEqualToIgnoringFields(expected, ignorePrivateFields, property, *others).test(actual)
      if(result.passed()) return EqualityResult.equal(actual, expected, this)
      return EqualityResult.notEqual(actual, expected, this).withDetails { result.failureMessage() }
   }

   override fun toString(): String = name()
}

fun <T : Any> Equality.Companion.byReflectionIgnoringFields(
   property: KProperty<*>,
   vararg others: KProperty<*>,
   ignorePrivateFields: Boolean = true,
) = ReflectionIgnoringFieldsEquality<T>(
   property = property,
   others = others,
   ignorePrivateFields = ignorePrivateFields
)
