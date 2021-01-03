package io.kotest.engine.launchers

import io.kotest.core.spec.Spec
import io.kotest.engine.spec.SpecExecutor
import kotlin.reflect.KClass

/**
 * A [SpecLauncher] is responsible for launching the given list of specs into their own coroutines.
 *
 * See [DefaultSpecLauncher] for the default implementation.
 */
interface SpecLauncher {
   suspend fun launch(executor: SpecExecutor, specs: List<KClass<out Spec>>)
}


