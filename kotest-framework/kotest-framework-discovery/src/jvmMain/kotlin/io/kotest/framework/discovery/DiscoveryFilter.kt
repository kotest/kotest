package io.kotest.framework.discovery

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

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
}
