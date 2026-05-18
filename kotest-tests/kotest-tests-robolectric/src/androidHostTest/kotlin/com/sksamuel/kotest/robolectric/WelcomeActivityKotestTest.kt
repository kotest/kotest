package com.sksamuel.kotest.robolectric

import android.content.Intent
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import io.kotest.tests.robolectric.LoginActivity
import io.kotest.tests.robolectric.WelcomeActivity
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

/**
 * Kotest version of `WelcomeActivityTest`. Exercises the same behaviour the
 * JUnit 4 / @RunWith test does, but routed through Kotest's engine and
 * Robolectric's `RobolectricExtension`.
 *
 * Opting in is just the [RobolectricTest] annotation - the extension itself
 * is registered globally via the project config (see `ProjectConfig.kt`),
 * but only specs annotated with `@RobolectricTest` actually get the sandbox.
 */
@RobolectricTest
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
