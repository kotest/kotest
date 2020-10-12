package com.sksamuel.robolectric

import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import org.robolectric.Robolectric

@RobolectricTest
class DummyRobolectricTest : FunSpec({
   test("Hello, Robo!") {
      ActivityScenario.launch(MainActivity::class.java)
      onView(withId(R.id.textview)).check(ViewAssertions.matches(withText("Kotest")))
   }
})
