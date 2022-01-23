package io.kotest.comparators

import io.kotest.equals.EqualityVerifier
import io.kotest.equals.EqualityVerifiers
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.equality.beEqualToIgnoringFields
import kotlin.reflect.KProperty

class ReflectionIgnoringFieldsComparator<T : Any>(
   private val property: KProperty<*>,
   private val others: Array<out KProperty<*>>,
   private val ignorePrivateFields: Boolean = true,
) : EqualityVerifier<T> {
   override fun name(): String {
      val plural = if (others.isNotEmpty()) "s" else ""
      val ignoringPrivate = if (ignorePrivateFields) "ignoring" else "not ignoring"
      return "reflection comparison ignoring field$plural ${arrayOf(property) + others} and $ignoringPrivate private fields"
   }

   fun includingPrivateFields(): ReflectionIgnoringFieldsComparator<T> {
      return withIgnorePrivateFields(false)
   }

   fun ignoringPrivateFields(): ReflectionIgnoringFieldsComparator<T> {
      return withIgnorePrivateFields(true)
   }

   private fun withIgnorePrivateFields(value: Boolean): ReflectionIgnoringFieldsComparator<T> {
      return ReflectionIgnoringFieldsComparator(
         property = property,
         others = others,
         ignorePrivateFields = value,
      )
   }

   override fun verify(actual: T, expected: T): MatcherResult =
      beEqualToIgnoringFields(expected, ignorePrivateFields, property, *others).test(actual)

   override fun toString(): String = name()
}

fun <T : Any> EqualityVerifiers.reflectionIgnoringFields(
   property: KProperty<*>,
   vararg others: KProperty<*>
) = ReflectionIgnoringFieldsComparator<T>(property, others)
