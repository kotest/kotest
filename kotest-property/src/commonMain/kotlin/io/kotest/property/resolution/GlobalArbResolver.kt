package io.kotest.property.resolution

import io.kotest.property.Arb
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A globally available [ArbResolver] that allows users to attach their own defaults to types.
 */
object GlobalArbResolver : ArbResolver {

   private val mappings = mutableMapOf<KType, Arb<*>>()

   inline fun <reified A> register(arb: Arb<*>) = register(typeOf<A>(), arb)

   fun register(type: KType, arb: Arb<*>) {
      mappings[type] = arb
   }

   override fun resolve(type: KType): Arb<*>? {
      return mappings[type]
   }
}
