package com.sksamuel.kotest.robolectric

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

// taken from https://robolectric.org/writing-a-test/
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
            assertEquals(expectedIntent.component, actual.component)
        }
    }
}
