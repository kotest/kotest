package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.robolectric.RobolectricExtension

/**
 * Registers [RobolectricExtension] for the whole test module.
 *
 * The extension itself is opt-in per spec via
 * [io.kotest.extensions.robolectric.RobolectricTest], so registering it
 * here has no effect on specs that aren't annotated. Specs that *are*
 * annotated get their construction routed through Robolectric's sandbox
 * classloader and every test wrapped in Robolectric's before/after
 * lifecycle.
 */
class ProjectConfig : AbstractProjectConfig() {
   override val extensions: List<Extension> = listOf(
      // Pass our local app package so Robolectric instruments WelcomeActivity / LoginActivity.
      // Without this, those classes are loaded by the parent classloader and fail to cast to
      // the sandbox-shadowed android.app.Activity at Robolectric.buildActivity().
      RobolectricExtension(instrumentedPackages = listOf("io.kotest.tests.robolectric")),
   )
}
