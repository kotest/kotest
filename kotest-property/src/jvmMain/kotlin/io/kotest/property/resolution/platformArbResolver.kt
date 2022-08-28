package io.kotest.property.resolution

import io.kotest.property.Arb
import io.kotest.property.arbitrary.targetDefaultForType
import kotlin.reflect.KType

actual fun platformArbResolver(): ArbResolver = JvmArbResolver

object JvmArbResolver : ArbResolver {
   override fun resolve(type: KType): Arb<*>? {
      return targetDefaultForType(emptyMap(), type)
   }
}
