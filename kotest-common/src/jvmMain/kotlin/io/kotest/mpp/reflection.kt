@file:JvmName("reflectionjvm")

package io.kotest.mpp

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.reflect

object JvmReflection : Reflection {

   private val fqns = mutableMapOf<KClass<*>, String?>()

   override fun fqn(kclass: KClass<*>): String? = fqns.getOrPut(kclass) { kclass.qualifiedName }

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

   override fun <T : Any> isEnumClass(kclass: KClass<T>): Boolean = kclass.isSubclassOf(Enum::class)

   override fun paramNames(fn: Function<*>): List<String>? = fn.reflect()?.parameters?.mapNotNull { it.name }

   override fun <T : Any> primaryConstructorMembers(klass: KClass<T>): List<Property> {
      // gets the parameters for the primary constructor and then associates them with the member callable
      val constructorParams = klass::primaryConstructor.get()?.parameters ?: emptyList()
      val membersByName = klass::members.get().associateBy(KCallable<*>::name)
      return constructorParams.mapNotNull { param ->
         membersByName[param.name]?.let { callable -> Property(callable.name, param.type) { callable.call(it) } }
      }
   }

   override fun <T : Any> newInstanceNoArgConstructor(klass: KClass<T>): T {
      return klass.java.newInstance()
   }
}

actual val reflection: Reflection = JvmReflection
