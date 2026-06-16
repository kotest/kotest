package io.kotest.assertions.eq

import kotlin.reflect.KClass

/**
 * An [EqResolver] that consults a map of per-call [Eq] overrides before delegating to a
 * [fallback] resolver (the global [DefaultEqResolver] by default).
 *
 * An override applies only when `actual::class == expected::class`; this mirrors the type-match
 * guard used by [DefaultEqResolver] for its own custom registrations and keeps existing cross-type
 * dispatch (e.g. `Int` vs `Long`, `null` vs non-null) intact.
 *
 * Used internally by [withEqs] to install per-call overrides on the active [EqContext] without
 * touching any global state.
 */
class LayeredEqResolver(
   private val overrides: Map<KClass<*>, Eq<*>>,
   private val fallback: EqResolver = DefaultEqResolver,
) : EqResolver {

   override fun resolve(actual: Any?, expected: Any?): Eq<out Any?> {
      if (actual != null && expected != null && actual::class == expected::class) {
         overrides[actual::class]?.let { return it }
      }
      return fallback.resolve(actual, expected)
   }
}
