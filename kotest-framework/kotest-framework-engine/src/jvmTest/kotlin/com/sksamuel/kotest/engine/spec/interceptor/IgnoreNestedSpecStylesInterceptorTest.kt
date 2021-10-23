package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.config.EmptyExtensionRegistry
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.interceptor.IgnoreNestedSpecStylesInterceptor

class IgnoreNestedSpecStylesInterceptorTest : FunSpec({
   test("IgnoreNestedSpecStylesInterceptor should skip any nested spec style") {

      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
         .intercept { error("boom") }
         .invoke(MyBehaviorSpec())

      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
         .intercept { error("boom") }
         .invoke(MyWordSpec())

      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
         .intercept { error("boom") }
         .invoke(MyFreeSpec())

      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
         .intercept { error("boom") }
         .invoke(MyDescribeSpec())
   }
})

private class MyBehaviorSpec : BehaviorSpec()
private class MyFreeSpec : FreeSpec()
private class MyWordSpec : WordSpec()
private class MyDescribeSpec : DescribeSpec()
