package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.NamedTag
import io.kotest.core.TagExpression
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Tags
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.ref.TagsInterceptor
import io.kotest.matchers.booleans.shouldBeTrue

@EnabledIf(LinuxCondition::class)
class TagsExcludedDiscoveryExtensionTest : FunSpec() {
   init {

      test("TagsExcludedSpecInterceptor should support include & exclude") {

         val tags = TagExpression.Empty.include(NamedTag("SpecIncluded")).exclude(NamedTag("SpecExcluded"))
         val conf = ProjectConfiguration()
         conf.registry.add(SpecifiedTagsTagExtension(tags))

         // will be excluded explicitly
         TagsInterceptor(NoopTestEngineListener, conf)
            .intercept(SpecRef.Reference(ExcludedSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  error("foo")
               }
            })

         // will be included as includes are ignored at the class level
         var executed = false
         TagsInterceptor(NoopTestEngineListener, conf)
            .intercept(SpecRef.Reference(IncludedSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  executed = true
                  return Result.success(emptyMap())
               }
            })
         executed.shouldBeTrue()

         // will be included as we must check the spec itself later to see if the test themselves have the include or exclude
         executed = false
         TagsInterceptor(NoopTestEngineListener, conf)
            .intercept(SpecRef.Reference(UntaggedSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  executed = true
                  return Result.success(emptyMap())
               }
            })
         executed.shouldBeTrue()
      }

      test("TagsExcludedSpecInterceptor should ignore include only") {

         val tags = TagExpression.Empty.include(NamedTag("SpecIncluded"))
         val conf = ProjectConfiguration()
         conf.registry.add(SpecifiedTagsTagExtension(tags))

         var executed = false
         TagsInterceptor(NoopTestEngineListener, conf)
            .intercept(SpecRef.Reference(ExcludedSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  executed = true
                  return Result.success(emptyMap())
               }
            })
         executed.shouldBeTrue()

         executed = false
         TagsInterceptor(NoopTestEngineListener, conf)
            .intercept(SpecRef.Reference(IncludedSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  executed = true
                  return Result.success(emptyMap())
               }
            })
         executed.shouldBeTrue()

         executed = false
         TagsInterceptor(NoopTestEngineListener, conf)
            .intercept(SpecRef.Reference(UntaggedSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  executed = true
                  return Result.success(emptyMap())
               }
            })
         executed.shouldBeTrue()
      }

      test("TagsExcludedSpecInterceptor should support exclude only") {

         val tags = TagExpression.Empty.exclude(NamedTag("SpecExcluded"))
         val conf = ProjectConfiguration()
         conf.registry.add(SpecifiedTagsTagExtension(tags))

         TagsInterceptor(NoopTestEngineListener, conf)
            .intercept(SpecRef.Reference(ExcludedSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  error("foo")
               }
            })

         var executed = false
         TagsInterceptor(NoopTestEngineListener, conf)
            .intercept(SpecRef.Reference(IncludedSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  executed = true
                  return Result.success(emptyMap())
               }
            })
         executed.shouldBeTrue()

         executed = false
         TagsInterceptor(NoopTestEngineListener, conf)
            .intercept(SpecRef.Reference(UntaggedSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  executed = true
                  return Result.success(emptyMap())
               }
            })
         executed.shouldBeTrue()
      }
   }
}

@Tags("SpecExcluded")
private class ExcludedSpec : ExpectSpec()

@Tags("SpecIncluded")
private class IncludedSpec : BehaviorSpec()

private class UntaggedSpec : FunSpec()
