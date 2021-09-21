package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

@ExperimentalStdlibApi
inline fun <reified T : Any> Arb.Companion.data(): Arb<T> =
   Arb.data(T::class)

@ExperimentalStdlibApi
fun <T : Any> Arb.Companion.data(clazz: KClass<T>): Arb<T> =
   if (clazz.isData) {
      val parameters: List<Arb<*>> = clazz.primaryConstructor
         ?.parameters
         ?.map { toArb(it, it.type) } ?: error("Data classes must have a primary constructor")

      arbitrary { rs ->
         clazz.primaryConstructor!!.call(
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
