package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.config.Configuration
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.interceptor.IgnoreNestedSpecStylesInterceptor

class IgnoreNestedSpecStylesInterceptorTest : FunSpec({
   test("IgnoreNestedSpecStylesInterceptor should skip any nested spec style") {

      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, Configuration())
         .intercept { error("boom") }
         .invoke(MyBehaviorSpec())

      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, Configuration())
         .intercept { error("boom") }
         .invoke(MyWordSpec())

      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, Configuration())
         .intercept { error("boom") }
         .invoke(MyFreeSpec())

      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, Configuration())
         .intercept { error("boom") }
         .invoke(MyDescribeSpec())
   }
})

private class MyBehaviorSpec : BehaviorSpec() {
   init {
   }
}

private class MyFreeSpec : FreeSpec() {
   init {
   }
}

private class MyWordSpec : WordSpec() {
   init {
   }
}

private class MyDescribeSpec : DescribeSpec() {
   init {
   }
}
