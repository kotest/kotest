package io.kotest.framework.discovery

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

sealed class DiscoverySelector {

   abstract fun test(kclass: KClass<out Spec>): Boolean

   data class ClassDiscoverySelector(val className: String) : DiscoverySelector() {
      override fun test(kclass: KClass<out Spec>): Boolean =
         className == (kclass.qualifiedName ?: kclass.java.canonicalName)
   }

   // accepts a package if it is a subpackage of the given name
   data class PackageDiscoverySelector(val packageName: String) : DiscoverySelector() {
      override fun test(kclass: KClass<out Spec>): Boolean {
         return packageName == kclass.java.`package`.name || kclass.java.`package`.name.startsWith("$packageName.")
      }
   }
}
