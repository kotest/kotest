package io.kotest.core.spec

import io.kotest.core.extensions.Extension
import kotlin.js.JsName

abstract class Extendable {

   // the registered extensions
   @JsName("extensions_js")
   private var _extensions = emptyList<Extension>()

   /**
    * Register one or more [Extension]s directly on this class.
    */
   fun extensions(extensions: List<Extension>) {
      _extensions = _extensions + extensions
   }

   /**
    * Returns any [Extension] instances registered directly on this class.
    */
   fun extensions(): List<Extension> {
      return _extensions.toList()
   }

   /**
    * Register a single [Extension] of type T and return that extension.
    */
   fun <T : Extension> extension(extension: T): T {
      extensions(extension)
      return extension
   }

   /**
    * Registers one or more [Extension]s.
    */
   fun extensions(vararg extensions: Extension) {
      require(extensions.isNotEmpty()) { "Cannot register empty list of extensions" }
      extensions(extensions.toList())
   }

   /**
    * Register [Extension]s to be invoked before all other extensions that have
    * been directly registered on this class.
    */
   fun prependExtensions(extensions: List<Extension>) {
      _extensions = extensions + _extensions
   }

   /**
    * Registers an [Extension] to be invoked before all other extensions that have
    * been directly registered on this class.
    */
   fun prependExtension(extension: Extension) {
      prependExtensions(listOf(extension))
   }
}
