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

// cannot be suspending as it is used in constructors
fun <CONFIG, MATERIALIZED> Spec.install(
   mountable: MountableExtension<CONFIG, MATERIALIZED>,
   configure: CONFIG.() -> Unit = {}
): MATERIALIZED {
   extensions(mountable)
   return mountable.mount(configure)
}

/**
 * An [InstallableExtension] is an [Extension] that can return a materialized value to the
 * user and allows a user defined configuration block.
 *
 * This allows extensions to return 'control' objects which differ from the extension itself.
 *
 * For example:
 *
 * class MyTest : FunSpec() {
 *  init {
 *   val ds = install(PostgresContainer) {
 *     user = "root"
 *     password = "letmein"
 *   }
 *  }
 * }
 *
 * Here `ds` is the materialized value. In this case it could be a DataSource that was
 * initialized from the extension.
 *
 * [InstallableExtension]s are suspendable so can only be installed inside a coroutine,
 * such as a test or callback. They cannot be invoked from the constructor aka class body.
 *
 */
interface InstallableExtension<CONFIG, MATERIALIZED> : Extension {
   suspend fun mount(configure: suspend CONFIG.() -> Unit): MATERIALIZED
}
