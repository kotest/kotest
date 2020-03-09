package io.kotest.core.engine.discovery

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility

/**
 * [DiscoveryRequest] describes how to discover test classes.
 *
 * Selectors are instances of [DiscoverySelector] and are used to locate [Spec]s. For example,
 * a Spec may be referred to by name via a [DiscoverySelector.ClassDiscoverySelector], or all specs in a
 * package may be referred to via a [DiscoverySelector.PackageDiscoverySelector]. Selectors stack, so each
 * selector may contribute zero or more specs and all discovered specs are returned.
 *
 * Filters are instances of [DiscoveryFilter] and are applied to the discovered set of Specs.
 * All of them have to include a resource for it to end up in the test plan. For example, you may
 * filter specs by a [DiscoveryFilter.ClassNameDiscoveryFilter] where any specs that do not have a matching name
 * are removed. In addition, you could apply a [DiscoveryFilter.PackageNameDiscoveryFilter] and all specs not in
 * the specified packages would be removed.
 */
data class DiscoveryRequest(
   val selectors: List<DiscoverySelector> = emptyList(),
   val filters: List<DiscoveryFilter> = emptyList()
)

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
    * Filters specs based on their [Modifier] values (public, internal, etc).
    * A Spec is included if it has a modifier that is included in the given set.
    */
   data class ClassModifierDiscoveryFilter(val modifiers: Set<Modifier>) : DiscoveryFilter() {
      override fun test(kclass: KClass<out Spec>): Boolean {
         if (kclass.visibility == KVisibility.INTERNAL)
            return modifiers.contains(Modifier.Internal)
         if (java.lang.reflect.Modifier.isPublic(kclass.java.modifiers))
            return modifiers.contains(Modifier.Public)
         return false
      }
   }
}

data class FullyQualifiedClassName(val value: String)
data class PackageName(val value: String)

enum class Modifier {
   Public, Internal
}

sealed class DiscoverySelector {

   abstract fun test(kclass: KClass<out Spec>): Boolean

   data class ClassDiscoverySelector(val className: String) : DiscoverySelector() {
      override fun test(kclass: KClass<out Spec>): Boolean =
         className == kclass.qualifiedName ?: kclass.java.canonicalName
   }

   data class PackageDiscoverySelector(val packageName: String) : DiscoverySelector() {
      override fun test(kclass: KClass<out Spec>): Boolean = packageName == kclass.java.`package`.name
   }
}
