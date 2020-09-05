@file:JvmName("reflectionjvm")

package io.kotest.mpp

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
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
   } catch (e: Throwable) {
      false
   }

   override fun paramNames(fn: Function<*>): List<String>? = fn.reflect()?.parameters?.mapNotNull { it.name }

   override fun <T : Any> primaryConstructorMembers(klass: KClass<T>): List<Property> {
      val constructorParams = klass::primaryConstructor.get()?.parameters ?: emptyList()
      val membersByName = klass::members.get().associateBy(KCallable<*>::name)
      return constructorParams.mapNotNull { param ->
         membersByName[param.name]?.let { callable -> Property(callable.name) { callable.call(it) } }
      }
   }

   override fun <T : Any> newInstanceNoArgConstructor(klass: KClass<T>): T {
      return klass.java.newInstance()
   }
}

actual val reflection: Reflection = JvmReflection
