package io.kotest.engine.launchers

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.engine.dispatchers.coroutineDispatcherFactory
import io.kotest.engine.extensions.DefaultTestSuiteScheduler
import io.kotest.engine.extensions.SpecLauncherExtension
import io.kotest.engine.spec.SpecExecutor
import io.kotest.fp.firstOrNone
import io.kotest.fp.getOrElse
import kotlin.reflect.KClass

/**
 * A [SpecLauncher] is responsible for launching the given list of specs into their own coroutines.
 *
 * See [DefaultTestSuiteScheduler] for the default implementation. Register a
 * [SpecLauncherExtension] to provide a custom implementation.
 */
@ExperimentalKotest
interface SpecLauncher {
   suspend fun launch(executor: SpecExecutor, specs: List<KClass<out Spec>>)
}

/**
 * Returns a [SpecLauncher] to be used for launching specs.
 *
 * Will use a [SpecLauncherExtension] if provided otherwise will default to the
 * [DefaultTestSuiteScheduler] using values provided by configuration.
 */
@ExperimentalKotest
fun specLauncher(): SpecLauncher {
   return configuration.extensions().filterIsInstance<SpecLauncherExtension>()
      .firstOrNone()
      .map { it.launcher() }
      .getOrElse {
         DefaultTestSuiteScheduler(
            configuration.concurrentSpecs ?: configuration.parallelism,
            coroutineDispatcherFactory()
         )
      }
}
