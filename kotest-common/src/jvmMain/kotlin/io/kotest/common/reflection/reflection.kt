@file:JvmName("reflectionjvm")

package io.kotest.common.reflection

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.jvm.reflect

object JvmReflection : Reflection {

   private val fqns = mutableMapOf<KClass<*>, String?>()
   private val annotations = mutableMapOf<Pair<KClass<*>, Set<AnnotationSearchParameter>>, List<Annotation>>()

   override fun fqn(kclass: KClass<*>): String? = fqns.getOrPut(kclass) { kclass.qualifiedName }

   override fun annotations(kclass: KClass<*>, parameters: Set<AnnotationSearchParameter>): List<Annotation> {
      return annotations.getOrPut(kclass to parameters) {
         val includeSuperclasses = parameters.contains(IncludingSuperclasses)
         val includeAnnotations = parameters.contains(IncludingAnnotations)
         annotations(kclass, includeSuperclasses, includeAnnotations)
      }
   }

   private fun annotations(
      kclass: KClass<*>,
      includeSuperclasses: Boolean,
      includeAnnotations: Boolean
   ): List<Annotation> {
      val classes = listOf(kclass) + if (includeSuperclasses) supers(kclass) else emptyList()
      return if (includeAnnotations) {
         classes.flatMap(::composedAnnotations)
      } else {
         classes.flatMap { it.annotationsSafe() }
      }
   }

   private fun supers(kclass: KClass<*>): Iterable<KClass<*>> {
      return kclass.superclasses + kclass.superclasses.flatMap { supers(it) }
   }

   private fun composedAnnotations(kclass: KClass<*>, checked: Set<String> = emptySet()): List<Annotation> {
      val annos = kclass.annotationsSafe()
      return annos + annos.flatMap {
         // we don't want to get into a loop with annotations that annotate themselves
         if (checked.contains(it.annotationClass.jvmName)) emptyList() else {
            composedAnnotations(it.annotationClass, checked + it.annotationClass.jvmName)
         }
      }
   }

   private fun KClass<*>.annotationsSafe(): List<Annotation> = try {
      this.annotations
   } catch (_: Exception) {
      emptyList()
   }

   override fun <T : Any> isDataClass(kclass: KClass<T>): Boolean = try {
      kclass.isData
   } catch (_: Throwable) {
      false
   }

   override fun <T : Any> isEnumClass(kclass: KClass<T>): Boolean = kclass.isSubclassOf(Enum::class)

   override fun paramNames(fn: Function<*>): List<String>? {
      @OptIn(ExperimentalReflectionOnLambdas::class)
      val fnReflect = fn.reflect()
      return fnReflect?.parameters?.mapNotNull { it.name }
   }

   override fun <T : Any> primaryConstructorMembers(klass: KClass<T>): List<Property> {
      // gets the parameters for the primary constructor and then associates them with the member callable
      val constructorParams = klass::primaryConstructor.get()?.parameters ?: emptyList()
      val membersByName = getPropertiesByName(klass)
      return constructorParams.mapNotNull { param ->
         membersByName[param.name]?.let { callable ->
            Property(callable.name, param.type) {
               callable.isAccessible = true
               callable.call(it)
            }
         }
      }
   }

   internal fun <T : Any> getPropertiesByName(klass: KClass<T>) = klass::members.get()
      .filterIsInstance<KProperty<*>>()
      .associateBy(KCallable<*>::name)
}

actual val reflection: Reflection = JvmReflection
