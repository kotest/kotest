package io.kotest.common.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility

actual val instantiations: Instantiations = ReflectionInstantiations

object ReflectionInstantiations : Instantiations {
   override fun <T : Any> newInstanceNoArgConstructorOrObjectInstance(kclass: KClass<T>): T {
      if (kclass.visibility == KVisibility.PRIVATE) error("Cannot use private class ${kclass.qualifiedName}")
      return when (val obj = kclass.objectInstance) {
         null -> kclass.java.getDeclaredConstructor().newInstance()
         else -> obj
      }
   }
}
