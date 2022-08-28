package io.kotest.core.extensions

import io.kotest.core.spec.Spec

/**
 * A [MountableExtension] is an [Extension] that can return a materialized value to the
 * user and allows for a configuration block.
 *
 * This allows extensions to return control objects which differ from the extension itself.
 *
 * For example:
 *
 * class MyTest : FunSpec() {
 *  init {
 *   val kafka = install(EmbeddedKafka) {
 *     port = 9092
 *   }
 *  }
 * }
 *
 * Here `kafka` is a materialized value that contains details of the host/port of the
 * started kafka instance and `EmbeddedKafka` is the extension itself.
 *
 */
interface MountableExtension<CONFIG, MATERIALIZED> : Extension {
   // cannot be suspending as it is invoked by install that is used in constructors
   fun mount(configure: CONFIG.() -> Unit): MATERIALIZED
}

/**
 * A [LazyMountableExtension] is an [Extension] that can return a materialized value to the
 * user and allows for a configuration block.
 *
 * This allows extensions to return control objects which differ from the extension itself.
 *
 * For example:
 *
 * class MyTest : FunSpec() {
 *  init {
 *   val kafka = install(EmbeddedKafka) {
 *     port = 9092
 *   }
 *  }
 * }
 *
 * Here `kafka` is a materialized value that contains details of the host/port of the
 * started kafka instance and `EmbeddedKafka` is the extension itself.
 *
 */
interface LazyMountableExtension<CONFIG, MATERIALIZED> : Extension {
   // cannot be suspending as it is invoked by install that is used in constructors
   fun mount(configure: CONFIG.() -> Unit): LazyMaterialized<MATERIALIZED>
}

data class LazyMaterialized<MATERIALIZED>(private var value: MATERIALIZED) {

   fun set(value: MATERIALIZED) {
      this.value = value
   }

   fun get() = value
}

// cannot be suspending as it is used in constructors
fun <CONFIG, MATERIALIZED> Spec.install(
   ext: MountableExtension<CONFIG, MATERIALIZED>,
   configure: CONFIG.() -> Unit = {}
): MATERIALIZED {
   extensions(ext)
   return ext.mount(configure)
}

// cannot be suspending as it is used in constructors
fun <CONFIG, MATERIALIZED> Spec.install(
   ext: LazyMountableExtension<CONFIG, MATERIALIZED>,
   configure: CONFIG.() -> Unit = {},
): LazyMaterialized<MATERIALIZED> {
   extensions(ext)
   return ext.mount(configure)
}
