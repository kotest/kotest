package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

@ExperimentalStdlibApi
fun <T : Any> Arb.Companion.data(marker: KClass<T>): Arb<T> =
   if (marker.isData) {
      val parameters: List<Arb<*>> = marker.primaryConstructor
         ?.parameters
         ?.map { toArb(it, it.type) } ?: error("Data classes must have a primary constructor")

      arbitrary { rs ->
         marker.primaryConstructor!!.call(
            *parameters.map { it.next(rs) }.toTypedArray()
         )
      }

   } else error("Only possible to use for data classes")

@ExperimentalStdlibApi
inline fun <reified T : Any> Arb.Companion.data(): Arb<T> =
   if (T::class.isData) {

      val parameters: List<Arb<*>> = T::class.primaryConstructor
         ?.parameters
         ?.map { toArb(it, it.type) } ?: error("Data classes must have a primary constructor")

      arbitrary { rs ->
         T::class.primaryConstructor!!.call(
            *parameters.map { it.next(rs) }.toTypedArray()
         )
      }

   } else error("Only possible to use for data classes")

@ExperimentalStdlibApi
fun toArb(property: KParameter, type: KType): Arb<*> {
   return arbForType(type) ?: error("Unhandled: ${property.name}")
}

@ExperimentalStdlibApi
fun arbForType(type: KType): Arb<*>? {
   return defaultForClass(type.classifier as KClass<*>)
      ?: tryDataArb(type)
      ?: targetDefaultForType(type)
}

@ExperimentalStdlibApi
fun tryDataArb(type: KType): Arb<*>? =
   if ((type.classifier as KClass<*>).isData) Arb.data(type.classifier as KClass<*>)
   else null
