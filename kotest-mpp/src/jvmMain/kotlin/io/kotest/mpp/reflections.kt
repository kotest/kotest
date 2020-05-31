@file:JvmName("reflectionjvm")

package io.kotest.mpp

import kotlin.reflect.KClass
import kotlin.reflect.jvm.reflect

actual val reflections: Reflections = JvmReflections

object JvmReflections : Reflections {

   override fun fqn(kclass: KClass<*>): String? = kclass.qualifiedName

   override fun annotations(kclass: KClass<*>): List<Annotation> {
      return try {
         kclass.annotations
      } catch (e: Exception) {
         emptyList()
      }
   }

   override fun isDataClass(kclass: KClass<*>): Boolean {
      return try {
         kclass.isData
      } catch (e: Exception) {
         false
      }
   }

   override fun paramNames(fn: Function<*>): List<String> =
      fn.reflect()?.parameters?.mapNotNull { it.name } ?: emptyList()
}
