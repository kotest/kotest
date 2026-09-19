package io.kotest.engine.extensions

import io.kotest.core.extensions.Extension
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
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

@OptIn(ExperimentalAtomicApi::class)
class DefaultExtensionRegistry : ExtensionRegistry {

   // get(kClass) is invoked twice per spec instantiation (once for constructor extensions, once
   // for post-instantiation extensions), which was previously an O(n) filter/allocation over every
   // registered extension on each call. Under IsolationMode.SingleInstance each spec class is only
   // instantiated once, so this doesn't matter there, but under InstancePerTest/InstancePerLeaf/
   // InstancePerRoot the same spec class is instantiated fresh per test/leaf/root, so without this
   // cache the same class's extension list would be re-filtered from scratch on every single test.
   //
   // add()/remove()/clear() can run concurrently with each other. Specs are inflated, and so
   // register their annotation-driven extensions, on their own coroutine, so under
   // SpecExecutionMode.Concurrent or TestExecutionMode.Concurrent, multiple calls can land on
   // different real threads at once. Rather than mutating a shared list in place, both the list
   // and its derived by-class index are held together in one immutable Snapshot and swapped
   // atomically via compare-and-set, so a mutation either fully applies or is retried. There is
   // no window where one thread can observe or corrupt a partially-updated list. get()/all() then
   // read the current snapshot directly, with no locking needed.
   private data class Snapshot(
      val extensions: List<Pair<Extension, KClass<*>?>>,
      val byClass: Map<KClass<*>?, List<Extension>>,
   )

   private val snapshot = AtomicReference(Snapshot(emptyList(), emptyMap()))

   override fun all(): List<Extension> = snapshot.load().extensions.map { it.first }

   override fun get(kClass: KClass<*>): List<Extension> = snapshot.load().byClass[kClass] ?: emptyList()

   override fun add(extension: Extension) {
      update { it + Pair(extension, null) }
   }

   override fun add(extension: Extension, kclass: KClass<*>) {
      update { it + Pair(extension, kclass) }
   }

   override fun remove(extension: Extension) {
      update { it - Pair(extension, null) }
   }

   override fun remove(extension: Extension, kclass: KClass<*>) {
      update { it - Pair(extension, kclass) }
   }

   override fun clear() {
      update { emptyList() }
   }

   private tailrec fun update(transform: (List<Pair<Extension, KClass<*>?>>) -> List<Pair<Extension, KClass<*>?>>) {
      val current = snapshot.load()
      val extensions = transform(current.extensions)
      val updated = Snapshot(extensions, extensions.groupBy({ it.second }, { it.first }))
      if (!snapshot.compareAndSet(current, updated)) {
         update(transform)
      }
   }

   override fun isEmpty(): Boolean = snapshot.load().extensions.isEmpty()
   override fun isNotEmpty(): Boolean = snapshot.load().extensions.isNotEmpty()
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
