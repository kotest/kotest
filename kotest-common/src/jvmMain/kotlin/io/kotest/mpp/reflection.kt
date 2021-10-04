@file:JvmName("reflectionjvm")

package io.kotest.mpp

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.jvm.reflect

object JvmReflection : Reflection {

   private val fqns = mutableMapOf<KClass<*>, String?>()
   private val annotations = mutableMapOf<KClass<*>, List<Annotation>>()

   override fun fqn(kclass: KClass<*>): String? = fqns.getOrPut(kclass) { kclass.qualifiedName }

   override fun annotations(kclass: KClass<*>, recursive: Boolean): List<Annotation> {
      return if (recursive) return annotations(kclass, emptySet()) else kclass.annotationsSafe()
   }

   private fun annotations(kclass: KClass<*>, checked: Set<String>): List<Annotation> {
      return annotations.getOrPut(kclass) {
         val annos = kclass.annotations
         annos + annos.flatMap {
            // we don't want to get into a loop with annotations that annotate themselves
            if (checked.contains(it.annotationClass.jvmName)) emptyList() else {
               annotations(it.annotationClass, checked + it.annotationClass.jvmName)
            }
         }
      }
   }

   private fun KClass<*>.annotationsSafe(): List<Annotation> = try {
      this.annotations
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

   // ignored because on JDK 8 newInstance is the only option
   @Suppress("DEPRECATION")
   override fun <T : Any> newInstanceNoArgConstructor(klass: KClass<T>): T {
      return klass.java.newInstance()
   }

   override fun <T : Any> newInstanceNoArgConstructorOrObjectInstance(klass: KClass<T>): T {
      return when (val obj = klass.objectInstance) {
         null -> newInstanceNoArgConstructor(klass)
         else -> obj
      }
   }
}

actual val reflection: Reflection = JvmReflection
