package io.kotest.common.reflection

import kotlin.reflect.KClass

actual val instantiations: Instantiations = ReflectionInstantiations

// ignored because on JDK 8 newInstance is the only option
@Suppress("DEPRECATION")
object ReflectionInstantiations : Instantiations {
   override fun <T : Any> newInstanceNoArgConstructorOrObjectInstance(kclass: KClass<T>): T {
      return when (val obj = kclass.objectInstance) {
         null -> kclass.java.newInstance()
         else -> obj
      }
   }
}
