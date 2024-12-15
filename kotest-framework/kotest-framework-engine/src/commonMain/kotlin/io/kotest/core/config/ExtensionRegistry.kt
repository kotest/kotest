package io.kotest.core.config

import io.kotest.common.KotestInternal
import io.kotest.core.extensions.Extension

@KotestInternal
interface ExtensionRegistry {
   fun all(): List<Extension>
   fun add(extension: Extension)
   fun remove(extension: Extension)
   fun clear()
   fun isEmpty(): Boolean
   fun isNotEmpty(): Boolean
}

@KotestInternal
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

@KotestInternal
object EmptyExtensionRegistry : ExtensionRegistry {

   override fun all(): List<Extension> = emptyList()

   override fun add(extension: Extension) {
      throw UnsupportedOperationException()
   }

   override fun remove(extension: Extension) {
      throw UnsupportedOperationException()
   }

   override fun clear() {
      throw UnsupportedOperationException()
   }

   override fun isEmpty(): Boolean = true
   override fun isNotEmpty(): Boolean = false
}

@KotestInternal
class FixedExtensionRegistry(private vararg val extensions: Extension) : ExtensionRegistry {

   override fun all(): List<Extension> = extensions.toList()

   override fun add(extension: Extension) {
      throw UnsupportedOperationException()
   }

   override fun remove(extension: Extension) {
      throw UnsupportedOperationException()
   }

   override fun clear() {
      throw UnsupportedOperationException()
   }

   override fun isEmpty(): Boolean = extensions.isEmpty()
   override fun isNotEmpty(): Boolean = extensions.isNotEmpty()
}
