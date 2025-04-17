//package com.sksamuel.kotest.engine.interceptors
//
//import io.kotest.core.annotation.EnabledIf
//import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
//import io.kotest.core.config.AbstractProjectConfig
//import io.kotest.core.spec.SpecRef
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.TestResult
//import io.kotest.engine.config.ProjectConfigResolver
//import io.kotest.engine.listener.NoopTestEngineListener
//import io.kotest.engine.spec.SpecExtensions
//import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
//import io.kotest.engine.spec.interceptor.ref.DescriptorFilterSpecRefInterceptor
//import io.kotest.matchers.booleans.shouldBeFalse
//import io.kotest.matchers.booleans.shouldBeTrue
//import kotlin.reflect.KClass
//
//@EnabledIf(LinuxOnlyGithubCondition::class)
//class SpecFilterInterceptorTest : FunSpec() {
//   init {
//      test("spec filter's should filter tests") {
//         val p = object : AbstractProjectConfig() {
//            override val extensions = listOf(object : SpecFilter {
//               override fun filter(kclass: KClass<*>): SpecFilterResult {
//                  return if (kclass.simpleName == "FooSpec") SpecFilterResult.Exclude("foo") else SpecFilterResult.Include
//               }
//            })
//         }
//         var fired = false
//         DescriptorFilterSpecRefInterceptor(NoopTestEngineListener, ProjectConfigResolver(p), SpecExtensions())
//            .intercept(
//               SpecRef.Reference(FooSpec::class),
//               object : NextSpecRefInterceptor {
//                  override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
//                     fired = true
//                     return Result.success(emptyMap())
//                  }
//               })
//         fired.shouldBeFalse()
//
//         DescriptorFilterSpecRefInterceptor(NoopTestEngineListener, ProjectConfigResolver(p), SpecExtensions())
//            .intercept(
//               SpecRef.Reference(BarSpec::class),
//               object : NextSpecRefInterceptor {
//                  override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
//                     fired = true
//                     return Result.success(emptyMap())
//                  }
//               })
//         fired.shouldBeTrue()
//      }
//   }
//}
//
//private class FooSpec : FunSpec()
//private class BarSpec : FunSpec()
