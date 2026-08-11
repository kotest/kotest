package io.kotest.engine.extensions

import io.kotest.core.extensions.Extension
import kotlin.concurrent.Volatile
import kotlin.reflect.KClass

/**
 * An [ExtensionRegistry] is a collection of [Extension]s that can be added to or removed from.
 *
 * This is used to manage extensions that are added via annotations and other mechanisms that are
 * not added programatically eg through a spec itself, or project config.
 *
 */
interface ExtensionRegistry {

   fun all(): List<Extension>

   /**
    * Returns all extensions that are registered to a specific kclass.
    */
   fun get(kClass: KClass<*>): List<Extension>

   /**
    * Adds a global [Extension] to this registry.
    * A global extension will be available to all specs.
    */
   fun add(extension: Extension)

   /**
    * Adds a restricted [Extension] to this registry.
    * A restricted extension is only available to the registered spec class.
    */
   fun add(extension: Extension, kclass: KClass<*>)

   fun remove(extension: Extension)
   fun remove(extension: Extension, kclass: KClass<*>)

   fun clear()
   fun isEmpty(): Boolean
   fun isNotEmpty(): Boolean
}

class DefaultExtensionRegistry : ExtensionRegistry {

   private val extensions = mutableListOf<Pair<Extension, KClass<*>?>>()

   // get(kClass) is invoked twice per spec instantiation (once for constructor extensions, once
   // for post-instantiation extensions), which was previously an O(n) filter/allocation over every
   // registered extension on each call. Under IsolationMode.SingleInstance each spec class is only
   // instantiated once, so this doesn't matter there, but under InstancePerTest/InstancePerLeaf/
   // InstancePerRoot the same spec class is instantiated fresh per test/leaf/root, so without this
   // cache the same class's extension list would be re-filtered from scratch on every single test.
   // Extensions are normally all registered up front, before specs start executing, so we rebuild
   // this snapshot on every mutation and let get() do a plain O(1) read. That also keeps get() safe
   // to call concurrently (e.g. under SpecExecutionMode.Concurrent) without needing to synchronize
   // reads, since add/remove are not expected to race with spec execution.
   @Volatile
   private var byClass: Map<KClass<*>?, List<Extension>> = emptyMap()

   override fun all(): List<Extension> = extensions.map { it.first }

   override fun get(kClass: KClass<*>): List<Extension> = byClass[kClass] ?: emptyList()

   override fun add(extension: Extension) {
      extensions.add(Pair(extension, null))
      rebuild()
   }

   override fun add(extension: Extension, kclass: KClass<*>) {
      extensions.add(Pair(extension, kclass))
      rebuild()
   }

   override fun remove(extension: Extension) {
      extensions.remove(Pair(extension, null))
      rebuild()
   }

   override fun remove(extension: Extension, kclass: KClass<*>) {
      extensions.remove(Pair(extension, kclass))
      rebuild()
   }

   override fun clear() {
      extensions.clear()
      rebuild()
   }

   private fun rebuild() {
      byClass = extensions.groupBy({ it.second }, { it.first })
   }

   override fun isEmpty(): Boolean = extensions.isEmpty()
   override fun isNotEmpty(): Boolean = extensions.isNotEmpty()
}

object EmptyExtensionRegistry : ExtensionRegistry {

   override fun all(): List<Extension> = emptyList()
   override fun get(kClass: KClass<*>): List<Extension> = emptyList()

   override fun add(extension: Extension) {
      throw UnsupportedOperationException("Cannot add to an empty extension registry")
   }

   override fun add(extension: Extension, kclass: KClass<*>) {
      throw UnsupportedOperationException("Cannot add to an empty extension registry")
   }

   override fun remove(extension: Extension) {
   }

   override fun remove(extension: Extension, kclass: KClass<*>) {
   }

   override fun clear() {
   }

   override fun isEmpty(): Boolean = true
   override fun isNotEmpty(): Boolean = false
}
