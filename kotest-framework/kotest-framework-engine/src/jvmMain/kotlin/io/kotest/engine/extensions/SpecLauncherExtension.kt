package io.kotest.engine.extensions

import io.kotest.engine.launchers.SpecLauncher

/**
 * An extension point that can be used to return a custom [SpecLauncher].
 */
interface SpecLauncherExtension {
   fun launcher(): SpecLauncher
}
