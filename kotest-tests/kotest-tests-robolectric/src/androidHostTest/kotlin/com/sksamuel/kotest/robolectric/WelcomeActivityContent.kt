package com.sksamuel.kotest.robolectric

import android.content.Intent
import io.kotest.extensions.robolectric.RobolectricFunSpec
import io.kotest.matchers.shouldBe
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

/**
 * Robolectric test content for [WelcomeActivity].
 *
 * This class is loaded by the Robolectric sandbox classloader (because its package
 * does not begin with "io.kotest."), so Android API calls use Robolectric's fully
 * instrumented implementations rather than the stub-only android.jar.
 */
class WelcomeActivityContent : RobolectricFunSpec({
   test("clicking login should start LoginActivity") {
      Robolectric.buildActivity(WelcomeActivity::class.java).use { controller ->
         controller.setup() // Moves the Activity to the RESUMED state

         val activity = controller.get()
         activity.loginButton.performClick()

         val expectedIntent = Intent(activity, LoginActivity::class.java)
         val actual = shadowOf(RuntimeEnvironment.getApplication()).nextStartedActivity
         actual.component shouldBe expectedIntent.component
      }
   }
})
