package com.sksamuel.kotest.robolectric

import android.content.Intent
import io.kotest.matchers.shouldBe
import io.kotest.tests.robolectric.LoginActivity
import io.kotest.tests.robolectric.WelcomeActivity
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf


/**
 * We want to write a test that asserts that when the user clicks on the "Login" button, the app
 * launches the LoginActivity.
 *
 * To achieve this, we can check that the correct Intent is started when the click is performed.
 * Since Robolectric is a unit testing framework, the LoginActivity will not actually be started.
 */

// Robolectric requires the JUnit4 runner (@RunWith). The JUnit Vintage Engine
// bridges this to JUnit Platform, allowing it to run alongside Kotest specs.
@RunWith(RobolectricTestRunner::class)
class WelcomeActivityTest {

   @Test
   fun clickingLogin_shouldStartLoginActivity() {
      Robolectric.buildActivity(WelcomeActivity::class.java).use { controller ->
         controller.setup() // Moves the Activity to the RESUMED state

         val activity = controller.get()
         activity.loginButton.performClick()

         val expectedIntent = Intent(activity, LoginActivity::class.java)
         val actual = shadowOf(RuntimeEnvironment.getApplication()).nextStartedActivity
         actual.component shouldBe expectedIntent.component
      }
   }
}
