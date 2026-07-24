@file:Suppress("UNCHECKED_CAST")

package io.kotest.engine

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.isAccessible

// resolving the zero-arg constructor via kclass.constructors.find { ... } is a reflective scan.
// Under IsolationMode.SingleInstance each spec class is only instantiated once, so this cache
// doesn't matter there, but under InstancePerTest/InstancePerLeaf/InstancePerRoot the same spec
// class is instantiated fresh per test/leaf/root, so without this cache the same class's
// constructor would be re-resolved from scratch on every single test in that spec.
private val constructorCache = ConcurrentHashMap<KClass<*>, KFunction<*>>()

/**
 * Instantiates an instance of the given class, or if it is an object, returns that object instance
 */
internal fun <T : Any> instantiateOrObject(kclass: KClass<T>): Result<T> {

   val obj = kclass.objectInstance
   if (obj != null) return Result.success(obj)

   return runCatching {
      val zeroArgsConstructor = (constructorCache.getOrPut(kclass) {
         kclass.constructors.find { it.parameters.isEmpty() }
            ?: throw IllegalArgumentException("Class ${kclass.simpleName} should have a zero-arg constructor")
      } as KFunction<T>).also { it.isAccessible = true }
      zeroArgsConstructor.call()
   }.onFailure {
      it.printStackTrace()
   }
}
