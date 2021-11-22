package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.common.ExperimentalKotest
import io.kotest.core.NamedTag
import io.kotest.core.TagExpression
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.engine.spec.interceptor.TagsExcludedSpecInterceptor
import io.kotest.matchers.booleans.shouldBeTrue

@ExperimentalKotest
class TagsExcludedDiscoveryExtensionTest : FunSpec() {
   init {

      test("TagsExcludedSpecInterceptor should support include & exclude") {

         val tags = TagExpression.Empty.include(NamedTag("SpecIncluded")).exclude(NamedTag("SpecExcluded"))
         val conf = ProjectConfiguration()
         conf.registry().add(SpecifiedTagsTagExtension(tags))

         // will be excluded explicitly
         TagsExcludedSpecInterceptor(NoopTestEngineListener, conf)
            .intercept(ReflectiveSpecRef(ExcludedSpec::class)) { error("foo") }

         // will be included as includes are ignored at the class level
         var executed = false
         TagsExcludedSpecInterceptor(NoopTestEngineListener, conf)
            .intercept(ReflectiveSpecRef(IncludedSpec::class)) {
               executed = true
               Result.success(emptyMap())
            }
         executed.shouldBeTrue()

         // will be included as we can must check the spec itself later to see if the test themselves have the include or exclude
         executed = false
         TagsExcludedSpecInterceptor(NoopTestEngineListener, conf)
            .intercept(ReflectiveSpecRef(UntaggedSpec::class)) {
               executed = true
               Result.success(emptyMap())
            }
         executed.shouldBeTrue()
      }

      test("TagsExcludedSpecInterceptor should ignore include only") {

         val tags = TagExpression.Empty.include(NamedTag("SpecIncluded"))
         val conf = ProjectConfiguration()
         conf.registry().add(SpecifiedTagsTagExtension(tags))

         var executed = false
         TagsExcludedSpecInterceptor(NoopTestEngineListener, conf)
            .intercept(ReflectiveSpecRef(ExcludedSpec::class)) {
               executed = true
               Result.success(emptyMap())
            }
         executed.shouldBeTrue()

         executed = false
         TagsExcludedSpecInterceptor(NoopTestEngineListener, conf)
            .intercept(ReflectiveSpecRef(IncludedSpec::class)) {
               executed = true
               Result.success(emptyMap())
            }
         executed.shouldBeTrue()

         executed = false
         TagsExcludedSpecInterceptor(NoopTestEngineListener, conf)
            .intercept(ReflectiveSpecRef(UntaggedSpec::class)) {
               executed = true
               Result.success(emptyMap())
            }
         executed.shouldBeTrue()
      }

      test("TagsExcludedSpecInterceptor should support exclude only") {

         val tags = TagExpression.Empty.exclude(NamedTag("SpecExcluded"))
         val conf = ProjectConfiguration()
         conf.registry().add(SpecifiedTagsTagExtension(tags))

         TagsExcludedSpecInterceptor(NoopTestEngineListener, conf)
            .intercept(ReflectiveSpecRef(ExcludedSpec::class)) {
               error("foo")
            }

         var executed = false
         TagsExcludedSpecInterceptor(NoopTestEngineListener, conf)
            .intercept(ReflectiveSpecRef(IncludedSpec::class)) {
               executed = true
               Result.success(emptyMap())
            }
         executed.shouldBeTrue()

         executed = false
         TagsExcludedSpecInterceptor(NoopTestEngineListener, conf)
            .intercept(ReflectiveSpecRef(UntaggedSpec::class)) {
               executed = true
               Result.success(emptyMap())
            }
         executed.shouldBeTrue()
      }
   }
}

@io.kotest.core.annotation.Tags("SpecExcluded")
private class ExcludedSpec : ExpectSpec()

@io.kotest.core.annotation.Tags("SpecIncluded")
private class IncludedSpec : BehaviorSpec()

private class UntaggedSpec : FunSpec()
