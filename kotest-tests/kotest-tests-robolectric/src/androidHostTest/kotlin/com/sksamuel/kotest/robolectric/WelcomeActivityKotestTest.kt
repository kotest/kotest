package com.sksamuel.kotest.robolectric

import android.content.Intent
import com.sksamuel.kotest.tests.robolectric.LoginActivity
import com.sksamuel.kotest.tests.robolectric.WelcomeActivity
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricExtension
import io.kotest.matchers.shouldBe
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * Kotest version of `WelcomeActivityTest`. Exercises the same behaviour the
 * JUnit 4 / @RunWith test does, but routed through Kotest's engine and
 * Robolectric's `RobolectricExtension`.
 *
 * Opt-in is via [ApplyExtension] — the Kotest equivalent of JUnit 4's
 * `@RunWith(RobolectricTestRunner::class)`. The Robolectric `@Config`
 * annotation supplies the package(s) the sandbox should instrument; the
 * extension reads it the same way `RobolectricTestRunner` does.
 */
@ApplyExtension(RobolectricExtension::class)
@Config(instrumentedPackages = ["com.sksamuel.kotest.tests.robolectric"])
class WelcomeActivityKotestTest : FunSpec({

   test("clicking login should start LoginActivity") {
      Robolectric.buildActivity(WelcomeActivity::class.java).use { controller ->
         controller.setup()

         val activity = controller.get()
         activity.loginButton.performClick()

         val expectedIntent = Intent(activity, LoginActivity::class.java)
         val actual = shadowOf(RuntimeEnvironment.getApplication()).nextStartedActivity
         actual.component shouldBe expectedIntent.component
      }
   }
})
