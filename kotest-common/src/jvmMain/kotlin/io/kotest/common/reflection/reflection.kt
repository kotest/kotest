@file:JvmName("reflectionjvm")

package io.kotest.common.reflection

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmName

object JvmReflection : Reflection {

   // these caches are accessed concurrently during spec discovery/ordering/enabled-checks, so they
   // use ConcurrentHashMap to avoid the data races a plain HashMap would suffer under concurrency.
   private val fqns = ConcurrentHashMap<KClass<*>, String>()
   private val annotations = ConcurrentHashMap<Pair<KClass<*>, Set<AnnotationSearchParameter>>, List<Annotation>>()

   override fun fqn(kclass: KClass<*>): String? {
      // ConcurrentHashMap cannot store null values, and qualifiedName is null for local/anonymous
      // classes, so those are simply not cached (recomputing is cheap). get and put are each atomic;
      // concurrent computation of the same key just recomputes an identical value, which is harmless.
      fqns[kclass]?.let { return it }
      val name = kclass.qualifiedName ?: return null
      fqns[kclass] = name
      return name
   }

   override fun annotations(kclass: KClass<*>, parameters: Set<AnnotationSearchParameter>): List<Annotation> {
      return annotations.computeIfAbsent(kclass to parameters) {
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
      val classes = listOf(kclass) + if (includeSuperclasses) kclass.allSuperclasses else emptyList()
      return if (includeAnnotations) {
         classes.flatMap(::composedAnnotations)
      } else {
         classes.flatMap { it.annotationsSafe() }
      }
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

   override fun isAbstract(kclass: KClass<*>): Boolean = try {
      kclass.isAbstract
   } catch (_: Throwable) {
      false
   }

   override fun <T : Any> isEnumClass(kclass: KClass<T>): Boolean = kclass.isSubclassOf(Enum::class)

   override fun <T : Any> primaryConstructorMembers(klass: KClass<T>): List<Property> {
      // gets the parameters for the primary constructor and then associates them with the member callable
      val constructorParams = klass.primaryConstructor?.parameters ?: emptyList()
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

   internal fun <T : Any> getPropertiesByName(klass: KClass<T>) = klass.members
      .filterIsInstance<KProperty<*>>()
      .associateBy(KCallable<*>::name)
}

actual val reflection: Reflection = JvmReflection
