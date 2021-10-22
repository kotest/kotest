package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.Ignored
import io.kotest.core.config.Configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.engine.spec.interceptor.IgnoredSpecInterceptor

class IgnoredSpecInterceptorTest : FunSpec({
   test("IgnoredSpecInterceptor should skip any spec annotated with @Ignored") {
      IgnoredSpecInterceptor(NoopTestEngineListener, Configuration())
         .intercept { error("boom") }
         .invoke(ReflectiveSpecRef(MyIgnoredSpec::class))
   }
})

@Ignored
private class MyIgnoredSpec : FunSpec() {
   init {
      test("foo") { error("zapp!") }
   }
}
