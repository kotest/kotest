package io.kotest.matchers.equality

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

internal typealias PropertyPredicate = (KProperty<*>) -> Boolean

// If no java field exists, it is a computed property which only has a getter
internal val notComputed: PropertyPredicate = { it.javaField != null }

internal val notPrivate: PropertyPredicate = { it.visibility != KVisibility.PRIVATE }

internal infix fun PropertyPredicate.and(other: PropertyPredicate) =
   { property: KProperty<*> -> this(property) && other(property) }

/**
 * Returns the list of [PropertyPredicate]s to apply to the instances, based on
 * the options set on this [FieldEqualityConfig].
 */
internal fun FieldEqualityConfig.predicates(): List<PropertyPredicate> {
   return listOfNotNull(
      if (ignorePrivateFields) notPrivate else null,
      if (ignoreComputedFields) notComputed else null,
      { it !in excludedProperties },
      { it in includedProperties || includedProperties.isEmpty() },
   )
}

/**
 * Returns the member fields from the receiver, including only the the fields that
 * pass the supplied [predicates]. Note, a field must be included by all predicates to pass.
 */
internal fun <T> T.fields(predicates: List<PropertyPredicate>): List<KProperty1<out T, *>> {
   return this!!::class.memberProperties
      .asSequence()
      .onEach { it.isAccessible = true }
      .filter(predicates.reduce { a, b -> a.and(b) })
      .toList()
}
