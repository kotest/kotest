package io.kotest.property.resolution

import io.kotest.property.Arb
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Resolves a default [Arb] for reified type [A].
 * See [resolve].
 */
inline fun <reified A> Arb.Companion.default(): Arb<A> = resolve(typeOf<A>()) as Arb<A>

/**
 * Resolves a default [Arb] for a given [type] through the following
 * [ArbResolver] strategies:
 *
 * 1. Checks [CommonTypeArbResolver] for Kotlin common type mappings.
 * 2. Checks [platformArbResolver] for platform specific mappings.
 * 3. Checks user defined mappings in [GlobalArbResolver].
 *
 * Otherwise throws a [NoGeneratorFoundException].
 */
@Suppress("UNCHECKED_CAST")
fun resolve(type: KType): Arb<*> {
   return CommonTypeArbResolver.resolve(type)
      ?: platformArbResolver().resolve(type)
      ?: GlobalArbResolver.resolve(type)
      ?: throw NoGeneratorFoundException(
         """No default Arb could be found for $type.
         Pass explicit Arbs to your property test method or
         register global mappings with GlobalArbResolver.register<Type>(myarb)"""
      )
}

class NoGeneratorFoundException(msg: String) : RuntimeException(msg)

expect fun platformArbResolver(): ArbResolver
