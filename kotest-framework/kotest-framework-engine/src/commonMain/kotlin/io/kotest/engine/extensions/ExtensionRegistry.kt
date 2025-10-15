package io.kotest.engine.extensions

import io.kotest.core.extensions.Extension
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

   override fun all(): List<Extension> = extensions.map { it.first }

   override fun get(kClass: KClass<*>): List<Extension> {
      return extensions.filter { it.second == kClass }.map { it.first }
   }

   override fun add(extension: Extension) {
      extensions.add(Pair(extension, null))
   }

   override fun add(extension: Extension, kclass: KClass<*>) {
      extensions.add(Pair(extension, kclass))
   }

   override fun remove(extension: Extension) {
      extensions.remove(Pair(extension, null))
   }

   override fun remove(extension: Extension, kclass: KClass<*>) {
      extensions.remove(Pair(extension, kclass))
   }

   override fun clear() {
      extensions.clear()
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
