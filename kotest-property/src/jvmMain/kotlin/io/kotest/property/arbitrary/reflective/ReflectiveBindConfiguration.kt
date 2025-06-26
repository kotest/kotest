package io.kotest.property.arbitrary.reflective

import io.kotest.property.Arb
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

data class ClassConstructor<T : Any>(
   val kclass: KClass<T>,
   val constructor: KFunction<T>,
)

data class ReflectiveBindConfiguration(
   val providedArbs: Map<KClass<*>, Arb<*>>,
   val arbsForProps: Map<KProperty1<*, *>, Arb<*>>,
   val preferredClassContructors: List<ClassConstructor<*>>,
)

class ReflectiveBindConfigurationBuilder {
   private val _classArbs = mutableMapOf<KClass<*>, Arb<*>>()
   private val _propertyArbs = mutableMapOf<KProperty1<*, *>, Arb<*>>()

   val classArbs: Map<KClass<*>, Arb<*>> get() = _classArbs
   val propertyArbs: Map<KProperty1<*, *>, Arb<*>> get() = _propertyArbs

   @JvmName("bindClass")
   fun foo(mapping: Pair<KClass<*>, Arb<*>>) {
      _classArbs[mapping.first] = mapping.second
   }

   @JvmName("bindProperty")
   fun foo(mapping: Pair<KProperty1<*, *>, Arb<*>>) {
      _propertyArbs[mapping.first] = mapping.second
   }
   fun build() = ReflectiveBindConfiguration(
      providedArbs = _classArbs,
      arbsForProps = _propertyArbs,
      preferredClassContructors = emptyList(),
   )
}

