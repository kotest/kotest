package io.kotest.equals.types

import io.kotest.equals.EqualityResult
import io.kotest.equals.EqualityVerifier
import io.kotest.equals.EqualityVerifiers
import io.kotest.equals.areNotEqual

open class MapEqualityVerifier(
   private val strictNumberEquality: Boolean,
   private val ignoreCase: Boolean,
) : EqualityVerifier<Map<*, *>> {
   override fun name(): String = "map equality"

   override fun areEqual(actual: Map<*, *>, expected: Map<*, *>): EqualityResult {
      val equal = { EqualityResult.equal(actual, expected, this) }
      val notEqual = { EqualityResult.notEqual(actual, expected, this) }

      val actualKeys = actual.keys
      val expectedKeys = expected.keys

      val keysMissing = expectedKeys.subtract(actualKeys)
      val extraKeys = actualKeys.subtract(expectedKeys)
      val commonKeys = actualKeys.intersect(expectedKeys)

      val valuesVerifier = ObjectEqualsEqualityVerifier<Any?>(
         strictNumberEquality = strictNumberEquality,
         ignoreCase = ignoreCase,
      )

      val differentValues = commonKeys.mapNotNull { key ->
         val actualValue = actual[key]
         val expectedValue = expected[key]
         return@mapNotNull valuesVerifier.areEqual(actualValue, expectedValue).takeIf { it.areNotEqual() }?.let {
            Pair(key, it)
         }
      }

      if (keysMissing.isEmpty() && extraKeys.isEmpty() && differentValues.isEmpty()) {
         return equal()
      }

      val details = listOfNotNull(
         if (keysMissing.isEmpty()) null else "Some keys are missing: ${printValues(keysMissing)}",
         if (extraKeys.isEmpty()) null else "Some keys should not be there: ${printValues(keysMissing)}",
         if (differentValues.isEmpty()) null else "Some entries have different values: ${
            printValues(differentValues.map {
               """
               At key '${it.first}': ${it.second.details().explain()}

               """.trimIndent()
            })
         }",
      )

      return notEqual().withDetails {
         (listOf("Map contents are not equal by ${name()})") + details).joinToString(separator = "\n")
      }
   }

   fun withStrictNumberEquality() = copy(strictNumberEquality = true)
   fun withoutStrictNumberEquality() = copy(strictNumberEquality = false)
   fun ignoringCase() = copy(ignoreCase = true)
   fun caseSensitive() = copy(ignoreCase = false)

   private fun copy(
      strictNumberEquality: Boolean = this.strictNumberEquality,
      ignoreCase: Boolean = this.ignoreCase,
   ): MapEqualityVerifier {
      return MapEqualityVerifier(
         strictNumberEquality = strictNumberEquality,
         ignoreCase = ignoreCase
      )
   }

   private fun printValues(collection: Collection<*>): String {
      val max = 10
      val extra = collection.size - max
      return collection.joinToString(
         prefix = "[",
         separator = ", ",
         postfix = "${if (extra > 0) "... and $extra more" else ""}]"
      )
   }
}

fun EqualityVerifiers.mapEquality(
   strictNumberEquality: Boolean = false,
   ignoreCase: Boolean = false,
): MapEqualityVerifier = MapEqualityVerifier(
   strictNumberEquality = strictNumberEquality,
   ignoreCase = ignoreCase,
)
