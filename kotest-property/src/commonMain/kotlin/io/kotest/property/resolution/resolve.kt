package io.kotest.property.resolution

import io.kotest.property.Arb
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Resolves a default [Arb] for reified type [A].
 * See [resolve].
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified A> Arb.Companion.default(): Arb<A> = resolve(typeOf<A>()) as Arb<A>

/**
 * Resolves a default [Arb] for a given [type] through the following
 * [ArbResolver] strategies:
 *
 * 1. Checks user defined mappings in [GlobalArbResolver].
 * 2. Checks [CommonTypeArbResolver] for Kotlin common type mappings.
 * 3. Checks [platformArbResolver] for platform specific mappings.
 *
 * Otherwise throws a [NoGeneratorFoundException].
 */
@Suppress("UNCHECKED_CAST")
fun resolve(type: KType): Arb<*> {
   return GlobalArbResolver.resolve(type)
      ?: CommonTypeArbResolver.resolve(type)
      ?: platformArbResolver().resolve(type)
      ?: throw NoGeneratorFoundException(
         """No default Arb could be found for $type.
         Pass explicit Arbs to your property test method or
         register global mappings with GlobalArbResolver.register<Type>(myarb)""" + "\n\n"
      )
}

class NoGeneratorFoundException(msg: String) : RuntimeException(msg)

internal expect fun platformArbResolver(): ArbResolver
