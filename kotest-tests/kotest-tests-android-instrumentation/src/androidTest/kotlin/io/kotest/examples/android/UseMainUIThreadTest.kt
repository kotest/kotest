package io.kotest.examples.android

import androidx.test.platform.app.InstrumentationRegistry
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith

/**
 * Instrumented test using Kotest, which will execute on an Android device.
 *
 * Instrumented tests run on an Android device, either physical or emulated.
 * The app is built and installed alongside a test app that injects commands and reads the state.
 * Instrumented tests are usually UI tests, launching an app and then interacting with it.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(KotestTestRunner::class)
class UseMainUIThreadTest : ShouldSpec() {
   init {
      should("provide app context") {
         InstrumentationRegistry.getInstrumentation().runOnMainSync {
            Thread.currentThread().name shouldBe "main"
         }
      }
   }
}

@RunWith(KotestTestRunner::class)
class UseMainUIThreadTest2 : ShouldSpec() {
   init {
      should("provide app context") {
         InstrumentationRegistry.getInstrumentation().runOnMainSync {
            Thread.currentThread().name shouldBe "main"
         }
      }
   }
}
