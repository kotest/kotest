package io.kotest.engine.extensions

import io.kotest.core.config.ExperimentalKotest
import io.kotest.engine.launchers.SpecLauncher

/**
 * An extension point that can be used to return a custom [SpecLauncher].
 */
@ExperimentalKotest
interface SpecLauncherExtension {
   fun launcher(): SpecLauncher
}
