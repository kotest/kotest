package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.FunSpec
import kotlin.time.Duration.Companion.milliseconds

@Isolate
class CoroutinesTimeoutOverrideTest : FunSpec({

   coroutineTestScope = true

   val coroutinesTimeout = 10.milliseconds

   // Configure default test coroutines timeout
   beforeSpec { System.setProperty("kotlinx.coroutines.test.default_timeout", coroutinesTimeout.toString()) }
   afterSpec { System.clearProperty("kotlinx.coroutines.test.default_timeout") }

   // Issue: https://github.com/kotest/kotest/issues/3969
   test("timeout greater than coroutines timeout").config(
      timeout = coroutinesTimeout + 200.milliseconds,
   ) {
      realTimeDelay(coroutinesTimeout + 10.milliseconds)
   }

   test("invocation timeout greater than coroutines timeout").config(
      invocationTimeout = coroutinesTimeout + 200.milliseconds,
   ) {
      realTimeDelay(coroutinesTimeout + 10.milliseconds)
   }

   test("unspecified timeouts fallback to defaults").config(
      timeout = null,
      invocationTimeout = null,
   ) {
      realTimeDelay(coroutinesTimeout + 10.milliseconds)
   }
})
