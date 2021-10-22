package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.common.ExperimentalKotest
import io.kotest.core.annotation.Ignored
import io.kotest.core.config.Configuration
import io.kotest.core.listeners.IgnoredSpecListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.engine.spec.interceptor.IgnoredSpecInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.reflect.KClass

@ExperimentalKotest
class IgnoredSpecInterceptorTest : FunSpec({

   test("IgnoredSpecInterceptor should pass any class not annotated with @Ignored") {
      var fired = false
      IgnoredSpecInterceptor(NoopTestEngineListener, Configuration())
         .intercept {
            fired = true
            emptyMap()
         }.invoke(ReflectiveSpecRef(NotIgnoredSpec::class))
      fired.shouldBeTrue()
   }

   test("IgnoredSpecInterceptor should skip any spec annotated with @Ignored") {
      IgnoredSpecInterceptor(NoopTestEngineListener, Configuration())
         .intercept { error("boom") }
         .invoke(ReflectiveSpecRef(MyIgnoredSpec::class))
   }

   test("IgnoredSpecInterceptor should fire listeners on skip") {
      var fired = false
      val conf = Configuration()
      conf.register(object : IgnoredSpecListener {
         override suspend fun ignoredSpec(kclass: KClass<*>, reason: String?) {
            fired = true
         }
      })
      IgnoredSpecInterceptor(NoopTestEngineListener, conf)
         .intercept { error("boom") }
         .invoke(ReflectiveSpecRef(MyIgnoredSpec::class))
      fired.shouldBeTrue()
   }
})

private class NotIgnoredSpec : FunSpec()

@Ignored
private class MyIgnoredSpec : FunSpec()
