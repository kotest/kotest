package io.kotest.engine.js

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

class TimeoutTest : FunSpec() {
   init {

      aroundTest { (tc, fn) ->
         val result = fn(tc)
         if (tc.name.name == "JS engine should capture timeouts" && result.isErrorOrFailure)
            TestResult.Success(0.milliseconds)
         else
            result
      }

      //  the default JS timeout in Karma is 2000, so if this passes when delay is > 2000 we know Kotest
      // is setting timeouts correctly on the karma promises
      test("JS engine should set timeout on the underlying promise").config(timeout = 1.days) {
         delay(2500)
      }

      // the default JS timeout in Karma is 2000, but kotest should override that to the kotest default
      // if this test passes then we know that the default has been set
      test("JS engine should set default timeout on the underlying promise") {
         delay(3000)
      }

      // testing that we can set a very low timeout and capture it
      test("JS engine should capture timeouts").config(timeout = 1.milliseconds) {
         delay(10)
      }

      // tests timeouts with data testing in js
      withData("foo", "bar") {
         delay(3000)
      }
   }
}
