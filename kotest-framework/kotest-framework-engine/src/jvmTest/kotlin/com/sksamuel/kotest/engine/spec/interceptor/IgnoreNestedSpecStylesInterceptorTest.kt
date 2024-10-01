//package com.sksamuel.kotest.engine.spec.interceptor
//
//import io.kotest.core.config.EmptyExtensionRegistry
//import io.kotest.core.spec.style.BehaviorSpec
//import io.kotest.core.spec.style.DescribeSpec
//import io.kotest.core.spec.style.FreeSpec
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.core.spec.style.WordSpec
//import io.kotest.engine.listener.NoopTestEngineListener
//import io.kotest.engine.spec.interceptor.instance.IgnoreNestedSpecStylesInterceptor
//
//class IgnoreNestedSpecStylesInterceptorTest : FunSpec({
//   test("IgnoreNestedSpecStylesInterceptor should skip any nested spec style") {
//
//      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
//         .intercept(MyBehaviorSpec()) { error("boom") }
//
//      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
//         .intercept(MyWordSpec()) { error("boom") }
//
//      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
//         .intercept(MyFreeSpec()) { error("boom") }
//
//      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
//         .intercept(MyDescribeSpec()) { error("boom") }
//   }
//})
//
//private class MyBehaviorSpec : BehaviorSpec()
//private class MyFreeSpec : FreeSpec()
//private class MyWordSpec : WordSpec()
//private class MyDescribeSpec : DescribeSpec()
