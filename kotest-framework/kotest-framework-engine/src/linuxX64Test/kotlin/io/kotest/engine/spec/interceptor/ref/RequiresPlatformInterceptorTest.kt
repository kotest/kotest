package io.kotest.engine.spec.interceptor.ref

import io.kotest.core.Platform
import io.kotest.core.annotation.RequiresPlatform
import io.kotest.core.spec.style.FunSpec

// this is a JS only test inside the linux directory, so should not be invoked
@RequiresPlatform(Platform.JVM)
class RequiresPlatformInterceptorTest : FunSpec({
   test("should not execute") {
      error("wham!")
   }
})
