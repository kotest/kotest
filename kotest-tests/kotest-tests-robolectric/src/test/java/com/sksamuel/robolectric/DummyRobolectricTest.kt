package com.sksamuel.robolectric

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricTest

@RobolectricTest
class DummyRobolectricTest : FunSpec({
   test("Hello, Robo!") {
      ActivityScenario.launch(MainActivity::class.java)
      onView(withId(R.id.textview)).check(ViewAssertions.matches(withText("Kotest!")))
   }
})
