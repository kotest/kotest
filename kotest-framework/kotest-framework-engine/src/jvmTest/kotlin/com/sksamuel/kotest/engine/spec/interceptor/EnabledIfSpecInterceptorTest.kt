package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.common.ExperimentalKotest
import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.config.Configuration
import io.kotest.core.listeners.IgnoredSpecListener
import io.kotest.core.listeners.InactiveSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.engine.spec.interceptor.EnabledIfSpecInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.reflect.KClass

@ExperimentalKotest
class EnabledIfSpecInterceptorTest : FunSpec({

   test("EnabledIfSpecInterceptor should proceed for any spec not annotated with @EnabledIf") {
      var fired = false
      EnabledIfSpecInterceptor(NoopTestEngineListener, Configuration())
         .intercept {
            fired = true
            emptyMap()
         }.invoke(ReflectiveSpecRef(MyUnannotatedSpec::class))
      fired.shouldBeTrue()
   }

   test("EnabledIfSpecInterceptor should proceed any spec annotated with @EnabledIf that passes predicate") {
      var fired = false
      EnabledIfSpecInterceptor(NoopTestEngineListener, Configuration())
         .intercept {
            fired = true
            emptyMap()
         }.invoke(ReflectiveSpecRef(MyEnabledSpec::class))
      fired.shouldBeTrue()
   }

   test("EnabledIfSpecInterceptor should skip any spec annotated with @EnabledIf that fails predicate") {
      EnabledIfSpecInterceptor(NoopTestEngineListener, Configuration())
         .intercept { error("boom") }
         .invoke(ReflectiveSpecRef(MyDisabledSpec::class))
   }

   test("EnabledIfSpecInterceptor should fire listeners on skip") {
      var fired = false
      val conf = Configuration()
      conf.register(object : IgnoredSpecListener {
         override suspend fun ignoredSpec(kclass: KClass<*>, reason: String?) {
            fired = true
         }
      })
      EnabledIfSpecInterceptor(NoopTestEngineListener, conf)
         .intercept { error("boom") }
         .invoke(ReflectiveSpecRef(MyDisabledSpec::class))
      fired.shouldBeTrue()
   }
})

class MyEnabledCondition : EnabledCondition {
   override fun enabled(specKlass: KClass<out Spec>): Boolean = true
}

class MyDisabledCondition : EnabledCondition {
   override fun enabled(specKlass: KClass<out Spec>): Boolean = false
}


@EnabledIf(MyEnabledCondition::class)
private class MyEnabledSpec : FunSpec()

@EnabledIf(MyDisabledCondition::class)
private class MyDisabledSpec : FunSpec()

private class MyUnannotatedSpec : FunSpec()
