package io.kotest.property.resolution

import io.kotest.property.Arb
import kotlin.reflect.KType

/**
 * Looks up default [Arb] instances for types.
 */
interface ArbResolver {

   /**
    * Returns a default [Arb] for the given [KType] or null if one is not available.
    */
   fun resolve(type: KType): Arb<*>?
}

object EmptyArbResolver : ArbResolver {
   override fun resolve(type: KType): Arb<*>? = null
}
