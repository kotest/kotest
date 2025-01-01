@file:Suppress("UNCHECKED_CAST")

package io.kotest.engine

import kotlin.reflect.KClass
import kotlin.reflect.jvm.isAccessible

/**
 * Instantiates an instance of the given class, or if it is an object, returns that object instance
 */
internal fun <T : Any> instantiateOrObject(kclass: KClass<T>): Result<T> {

   val obj = kclass.objectInstance
   if (obj != null) return Result.success(obj)

   return runCatching {
      val zeroArgsConstructor = kclass.constructors.find { it.parameters.isEmpty() }
         ?: throw IllegalArgumentException("Class ${kclass.simpleName} should have a zero-arg constructor")
      zeroArgsConstructor.isAccessible = true
      zeroArgsConstructor.call()
   }.onFailure {
      it.printStackTrace()
   }
}
