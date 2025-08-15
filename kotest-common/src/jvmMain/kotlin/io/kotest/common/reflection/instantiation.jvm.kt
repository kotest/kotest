package io.kotest.common.reflection

import kotlin.reflect.KClass

actual val instantiations: Instantiations = ReflectionInstantiations

object ReflectionInstantiations : Instantiations {
   override fun <T : Any> newInstanceNoArgConstructorOrObjectInstance(kclass: KClass<T>): T {
      return when (val obj = kclass.objectInstance) {
         null -> kclass.java.getDeclaredConstructor().newInstance()
         else -> obj
      }
   }
}
