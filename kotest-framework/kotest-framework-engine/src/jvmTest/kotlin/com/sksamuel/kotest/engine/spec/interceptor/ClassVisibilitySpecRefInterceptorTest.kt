package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.common.Platform
import io.kotest.core.annotation.Ignored
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.project.TestSuite
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptors.ClassVisibilitySpecRefInterceptor
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class ClassVisibilitySpecRefInterceptorTest : FunSpec({

   test("ClassVisibilitySpecRefInterceptor should include private classes if only one") {
      var fired = false
      ClassVisibilitySpecRefInterceptor(
         EngineContext(ProjectConfiguration(), Platform.JVM).withTestSuite(
            TestSuite(
               listOf(SpecRef.Reference(PrivateSpec::class))
            )
         )
      ).intercept(SpecRef.Reference(PrivateSpec::class)) {
         fired = true
         Result.success(emptyMap())
      }
      fired.shouldBeFalse()
   }

   test("ClassVisibilitySpecRefInterceptor should exclude private classes if multiple") {
      var fired = false
      ClassVisibilitySpecRefInterceptor(
         EngineContext(ProjectConfiguration(), Platform.JVM).withTestSuite(
            TestSuite(
               listOf(
                  SpecRef.Reference(PrivateSpec::class),
                  SpecRef.Reference(PrivateSpec::class)
               ),
            )
         )
      ).intercept(SpecRef.Reference(PrivateSpec::class)) {
         fired = true
         Result.success(emptyMap())
      }
      fired.shouldBeFalse()
   }

   test("ClassVisibilitySpecRefInterceptor should include private classes when config flag is set") {
      var fired = false
      ClassVisibilitySpecRefInterceptor(
         EngineContext(ProjectConfiguration(), Platform.JVM).withTestSuite(
            TestSuite(
               listOf(
                  SpecRef.Reference(PrivateSpec::class),
                  SpecRef.Reference(PrivateSpec::class)
               ),
            )
         )
      ).intercept(SpecRef.Reference(PrivateSpec::class)) {
         fired = true
         Result.success(emptyMap())
      }
      fired.shouldBeTrue()
   }

   test("IgnoredSpecInterceptor should include internal classes by default") {
      var fired = false
      ClassVisibilitySpecRefInterceptor(EngineContext(ProjectConfiguration(), Platform.JVM))
         .intercept(SpecRef.Reference(InternalSpec::class)) {
            fired = true
            Result.success(emptyMap())
         }
      fired.shouldBeTrue()
   }

   test("IgnoredSpecInterceptor should include public classes by default") {
      var fired = false
      ClassVisibilitySpecRefInterceptor(EngineContext(ProjectConfiguration(), Platform.JVM))
         .intercept(SpecRef.Reference(PublicSpec::class)) {
            fired = true
            Result.success(emptyMap())
         }
      fired.shouldBeTrue()
   }
})

private class PrivateSpec : FunSpec() {
   init {
      test("a") {}
   }
}

@Ignored
internal class InternalSpec : FunSpec() {
   init {
      test("a") {}
   }
}

@Ignored
class PublicSpec : FunSpec() {
   init {
      test("a") {}
   }
}
