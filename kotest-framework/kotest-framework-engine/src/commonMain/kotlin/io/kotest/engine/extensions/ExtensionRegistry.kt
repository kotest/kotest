package io.kotest.engine.extensions

import io.kotest.core.extensions.Extension

/**
 * An [ExtensionRegistry] is a collection of [Extension]s that can be added to or removed from.
 *
 * This is used to manage extensions that are added via annotations and other mechanisms that are
 * not added programatically eg through a spec itself, or project config.
 *
 */
interface ExtensionRegistry {
   fun all(): List<Extension>
   fun add(extension: Extension)
   fun remove(extension: Extension)
   fun clear()
   fun isEmpty(): Boolean
   fun isNotEmpty(): Boolean
}

class DefaultExtensionRegistry : ExtensionRegistry {

   private val extensions = mutableListOf<Extension>()

   override fun all(): List<Extension> = extensions.toList()

   override fun add(extension: Extension) {
      extensions.add(extension)
   }

   override fun remove(extension: Extension) {
      extensions.remove(extension)
   }

   override fun clear() {
      extensions.clear()
   }

   override fun isEmpty(): Boolean = extensions.isEmpty()
   override fun isNotEmpty(): Boolean = extensions.isNotEmpty()
}
