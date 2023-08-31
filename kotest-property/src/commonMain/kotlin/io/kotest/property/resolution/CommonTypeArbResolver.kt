package io.kotest.property.resolution

import io.kotest.mpp.bestName
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uByte
import io.kotest.property.arbitrary.uInt
import io.kotest.property.arbitrary.uLong
import io.kotest.property.arbitrary.uShort
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Returns an [Arb] for well known common platform types such as Int, Long, String.
 *
 * Any types available on all platforms can be added here.
 */
@Suppress("UNCHECKED_CAST")
internal object CommonTypeArbResolver : ArbResolver {
   override fun resolve(type: KType): Arb<*>? {
      val kclass = type.classifier as? KClass<*> ?: return null
      val arb = when (kclass.bestName()) {
         "java.lang.String", "kotlin.String", "String" -> Arb.string()
         "java.lang.Character", "kotlin.Char", "Char" -> Arb.char()
         "java.lang.Long", "kotlin.Long", "Long" -> Arb.long()
         "kotlin.ULong", "ULong" -> Arb.uLong()
         "java.lang.Integer", "kotlin.Int", "Int" -> Arb.int()
         "kotlin.UInt", "UInt" -> Arb.uInt()
         "java.lang.Short", "kotlin.Short", "Short" -> Arb.short()
         "kotlin.UShort", "UShort" -> Arb.uShort()
         "java.lang.Byte", "kotlin.Byte", "Byte" -> Arb.byte()
         "kotlin.UByte", "UByte" -> Arb.uByte()
         "java.lang.Double", "kotlin.Double", "Double" -> Arb.double()
         "java.lang.Float", "kotlin.Float", "Float" -> Arb.float()
         "java.lang.Boolean", "kotlin.Boolean", "Boolean" -> Arb.boolean()
         else -> null
      }

      return if (type.isMarkedNullable) arb?.orNull() else arb
   }
}
