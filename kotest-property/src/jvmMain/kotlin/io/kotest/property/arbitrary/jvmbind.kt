package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

inline fun <reified T : Any> Arb.Companion.bind(): Arb<T> {
   val kclass = T::class
   require(kclass.isData)
   return arb { rs ->
      val arbs = kclass.primaryConstructor!!.parameters.map {
         it to (defaultForClass<Any>(it.type.classifier as KClass<*>)
            ?: error("Could not locate generator for parameter ${kclass.qualifiedName}.${it.name}"))
      }
      val iters = arbs.map { it.first to it.second.values(rs).iterator() }
      generateSequence {
         val values = iters.map {
            if (it.second.hasNext()) it.second.next().value else error("The generator for ${kclass.qualifiedName}.${it.first.name} has no more elements")
         }
         kclass.constructors.first().call(*values.toTypedArray())
      }
   }
}
