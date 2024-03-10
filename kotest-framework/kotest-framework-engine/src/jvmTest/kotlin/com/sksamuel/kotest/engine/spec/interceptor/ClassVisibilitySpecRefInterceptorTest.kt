package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.Ignored
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.interceptors.ClassVisibilitySpecRefInterceptor
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class ClassVisibilitySpecRefInterceptorTest : FunSpec({

   test("ClassVisibilitySpecRefInterceptor should ignore private classes by default") {
      var fired = false
      ClassVisibilitySpecRefInterceptor(ProjectConfiguration())
         .intercept(SpecRef.Reference(PrivateSpec::class)) {
            fired = true
            Result.success(emptyMap())
         }
      fired.shouldBeFalse()
   }

   test("ClassVisibilitySpecRefInterceptor should include private classes when config flag is set") {
      var fired = false
      ClassVisibilitySpecRefInterceptor(ProjectConfiguration().also { it.includePrivateClasses = true })
         .intercept(SpecRef.Reference(PrivateSpec::class)) {
            fired = true
            Result.success(emptyMap())
         }
      fired.shouldBeTrue()
   }

   test("IgnoredSpecInterceptor should include internal classes by default") {
      var fired = false
      ClassVisibilitySpecRefInterceptor(ProjectConfiguration())
         .intercept(SpecRef.Reference(InternalSpec::class)) {
            fired = true
            Result.success(emptyMap())
         }
      fired.shouldBeTrue()
   }

   test("IgnoredSpecInterceptor should include public classes by default") {
      var fired = false
      ClassVisibilitySpecRefInterceptor(ProjectConfiguration())
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
