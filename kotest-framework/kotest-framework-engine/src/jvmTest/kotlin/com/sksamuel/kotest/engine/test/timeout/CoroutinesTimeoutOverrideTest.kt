package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.FunSpec
import kotlin.time.Duration.Companion.milliseconds

@Isolate
class CoroutinesTimeoutOverrideTest : FunSpec({

   coroutineTestScope = true

   val coroutinesTimeout = 10.milliseconds

   // Configure default test coroutines timeout
   beforeSpec {
      System.setProperty("kotlinx.coroutines.test.default_timeout", coroutinesTimeout.toString())
   }

   afterSpec {
      System.clearProperty("kotlinx.coroutines.test.default_timeout")
   }

   // Issue: https://github.com/kotest/kotest/issues/3969
   test("kotest should pass the test timeout value to runTest when using coroutineTestScope").config(timeout = coroutinesTimeout * 100) {
      // if kotest was not passing the test timeout value to runTest, this test would fail with a timeout because
      // we are setting the default_timeout system property and waiting for longer than that
      realTimeDelay(coroutinesTimeout * 5)
   }

   test("invocation timeout greater than coroutines timeout").config(invocationTimeout = coroutinesTimeout * 100) {
      realTimeDelay(coroutinesTimeout * 5)
   }

   test("unspecified timeouts fallback to defaults").config(timeout = null, invocationTimeout = null) {
      realTimeDelay(coroutinesTimeout + 10.milliseconds)
   }
})
