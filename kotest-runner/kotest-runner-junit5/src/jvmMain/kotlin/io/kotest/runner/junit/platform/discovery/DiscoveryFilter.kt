package io.kotest.runner.junit.platform.discovery

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility

sealed class DiscoveryFilter {

   abstract fun test(kclass: KClass<out Spec>): Boolean

   /**
    * Filters specs based on the fully qualified class names.
    */
   data class ClassNameDiscoveryFilter(val f: (FullyQualifiedClassName) -> Boolean) : DiscoveryFilter() {
      override fun test(kclass: KClass<out Spec>): Boolean {
         return f(FullyQualifiedClassName(kclass.qualifiedName ?: kclass.java.canonicalName))
      }
   }

   /**
    * Filters specs based on their package.
    */
   data class PackageNameDiscoveryFilter(val f: (PackageName) -> Boolean) : DiscoveryFilter() {
      override fun test(kclass: KClass<out Spec>): Boolean {
         return f(PackageName(kclass.java.`package`.name))
      }
   }

   /**
    * Filters specs based on their [java.lang.reflect.Modifier] values (public, internal, etc).
    * A Spec is included if it has a modifier that is included in the given set.
    */
   data class ClassModifierDiscoveryFilter(val modifiers: Set<Modifier>) : DiscoveryFilter() {
      override fun test(kclass: KClass<out Spec>): Boolean {
         if (kclass.visibility == KVisibility.INTERNAL)
            return modifiers.contains(Modifier.Internal)
         if (kclass.visibility == KVisibility.PUBLIC || java.lang.reflect.Modifier.isPublic(kclass.java.modifiers))
            return modifiers.contains(Modifier.Public)
         if (kclass.visibility == KVisibility.PRIVATE || java.lang.reflect.Modifier.isPrivate(kclass.java.modifiers))
            return modifiers.contains(Modifier.Private)
         return false
      }
   }
}

data class FullyQualifiedClassName(val value: String)
data class PackageName(val value: String)

enum class Modifier {
   Public, Internal, Private
}
