package io.kotest.property.resolution

import io.kotest.property.Arb
import io.kotest.property.arbitrary.targetDefaultForType
import kotlin.reflect.KType

internal actual fun platformArbResolver(): ArbResolver = JvmArbResolver

object JvmArbResolver : ArbResolver {
   override fun resolve(type: KType): Arb<*>? {
      return targetDefaultForType(emptyMap(), emptyMap(),  type)
   }
}
