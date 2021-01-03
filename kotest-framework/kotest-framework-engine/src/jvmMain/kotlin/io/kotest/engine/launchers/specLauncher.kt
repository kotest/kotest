package io.kotest.engine.launchers

import io.kotest.core.config.configuration
import io.kotest.engine.dispatchers.coroutineDispatcherFactory
import io.kotest.engine.extensions.SpecLauncherExtension
import io.kotest.fp.firstOrNone
import io.kotest.fp.getOrElse

/**
 * Returns a [SpecLauncher] to be used for launching specs.
 *
 * Will use a [SpecLauncherExtension] if provided otherwise will default to the
 * [DefaultSpecLauncher] using values provided by configuration.
 */
fun specLauncher(): SpecLauncher {
   return configuration.extensions().filterIsInstance<SpecLauncherExtension>()
      .firstOrNone()
      .map { it.launcher() }
      .getOrElse {
         DefaultSpecLauncher(
            configuration.concurrentSpecs ?: configuration.parallelism,
            coroutineDispatcherFactory()
         )
      }
}
