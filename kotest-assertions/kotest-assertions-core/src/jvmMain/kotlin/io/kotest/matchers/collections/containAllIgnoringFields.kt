package io.kotest.matchers.collections

import io.kotest.inspectors.forAll
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.equality.beEqualToIgnoringFields
import io.kotest.matchers.should
import kotlin.reflect.KProperty


fun <T : Any> Iterable<T>.shouldContainAllIgnoringFields(
   ts: Collection<T>,
   field: KProperty<*>,
   vararg other: KProperty<*>
) = toList() should containAllIgnoringFields(ts = ts, property = field, other = other)

fun <T : Any> Array<T>.shouldContainAllIgnoringFields(
   ts: Collection<T>,
   field: KProperty<*>,
   vararg other: KProperty<*>
) = asList() should containAllIgnoringFields(ts = ts, property = field, other = other)

fun <T : Any> Collection<T>.shouldContainAllIgnoringFields(
   ts: Collection<T>,
   field: KProperty<*>,
   vararg other: KProperty<*>
) = this should containAllIgnoringFields(ts = ts, property = field, other = other)

fun <T : Any> containAllIgnoringFields(
   ts: Collection<T>,
   ignorePrivateFields: Boolean = false,
   property: KProperty<*>,
   vararg other: KProperty<*>
): Matcher<Collection<T>> = object :
   Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult {
      val missingItems = mutableListOf<T>()
      ts.forAll { tsItem ->
         val exist = value.any {
            val beEqualToIgnoringFields = beEqualToIgnoringFields(tsItem, ignorePrivateFields, property, *other)
            val matcherResult = beEqualToIgnoringFields.test(it)
            matcherResult.passed()

         }
         if (exist.not()) {
            missingItems.add(tsItem)
         }
      }
      val fields = listOf(property) + other
      val fieldsString = fields.joinToString(", ", "[", "]") { it.name }

      return MatcherResult(
         missingItems.isEmpty(),
         { "Collection should contain equals of $missingItems ignoring $fieldsString" },
         { "Collection should not contain equals of $missingItems ignoring $fieldsString" }
      )
   }
}
