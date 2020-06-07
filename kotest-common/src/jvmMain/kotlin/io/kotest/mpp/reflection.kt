@file:JvmName("reflectionjvm")

package io.kotest.mpp

import kotlin.reflect.KClass
import kotlin.reflect.jvm.reflect

object JvmReflection : Reflection {

   override fun fqn(kclass: KClass<*>): String? = kclass.qualifiedName

   override fun annotations(kclass: KClass<*>): List<Annotation> = try {
      kclass.annotations
   } catch (e: Exception) {
      emptyList()
   }

   override fun <T : Any> isDataClass(kclass: KClass<T>): Boolean = try {
      kclass.isData
   } catch (e: Exception) {
      false
   }

   override fun paramNames(fn: Function<*>): List<String>? = fn.reflect()?.parameters?.mapNotNull { it.name }

}

actual val reflection: Reflection = JvmReflection
