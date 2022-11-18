package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.interceptors.DumpConfigInterceptor
import io.kotest.matchers.shouldBe
import io.kotest.mpp.syspropOrEnv

class DumpConfigInterceptorTest : FunSpec({

   test("kotest.framework.dump.config") {
      when (syspropOrEnv(KotestEngineProperties.dumpConfig)) {
         "true" -> DumpConfigInterceptor.syspropEnabled() shouldBe true
         "false" -> DumpConfigInterceptor.syspropEnabled() shouldBe false
         else -> DumpConfigInterceptor.syspropEnabled() shouldBe false
      }
   }
})

