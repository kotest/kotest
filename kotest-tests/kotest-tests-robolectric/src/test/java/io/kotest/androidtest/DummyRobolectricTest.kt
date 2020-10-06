package io.kotest.androidtest    // Must be in this package as Robolectric runner ignores io.kotest package

import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe

@RobolectricTest
class DummyRobolectricTest : FunSpec({
   test("Hello, Robo!") {
      ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
         val textView = activity.findViewById<TextView>(R.id.textview)
         shouldThrow<AssertionError> { textView.text shouldBe "Katest!" }

         textView.text shouldBe "Kotest!"
      }
   }
})
