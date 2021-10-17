package io.kotest.core.extensions

import io.kotest.core.spec.Spec

/**
 * An installable extension that returns a materialized value to the user and allows for
 * a configuration block.
 *
 * This allows extensions to provide control objects which differ from the extension itself.
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
 * started kafka instance and `EmbeddedKafka` is an extension instance.
 *
 */
interface MountableExtension<CONFIG, MATERIALIZED> : Extension {
   // cannot be suspending as it is invoked by install that is used in constructors
   fun mount(configure: CONFIG.() -> Unit): MATERIALIZED
}

// cannot be suspending as it is used in constructors
fun <CONFIG, MATERIALIZED> Spec.install(
   mountable: MountableExtension<CONFIG, MATERIALIZED>,
   configure: CONFIG.() -> Unit = {}
): MATERIALIZED {
   extensions(mountable)
   return mountable.mount(configure)
}
