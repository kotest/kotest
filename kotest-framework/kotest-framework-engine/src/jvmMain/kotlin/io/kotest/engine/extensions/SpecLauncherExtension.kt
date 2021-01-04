package io.kotest.engine.extensions

import io.kotest.core.config.ExperimentalKotest
import io.kotest.core.extensions.Extension
import io.kotest.engine.launchers.SpecLauncher

/**
 * An extension point that can be used to return a custom [SpecLauncher].
 */
@ExperimentalKotest
interface SpecLauncherExtension : Extension {
   fun launcher(): SpecLauncher
}
