package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.NamedTag
import io.kotest.core.TagExpression
import io.kotest.core.config.EmptyExtensionRegistry
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.interceptor.ref.RequiresTagInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.coVerify
import io.mockk.spyk

class RequiresTagInterceptorTest : FunSpec() {
   init {

      test("RequiresTagInterceptor should include spec if the tag expression contains the required tag") {

         val tags = TagExpression.Empty.include(NamedTag("SpecTagged"))
         val conf = ProjectConfiguration()
         conf.registry.add(SpecifiedTagsTagExtension(tags))

         var executed = false
         RequiresTagInterceptor(NoopTestEngineListener, conf, EmptyExtensionRegistry)
            .intercept(SpecRef.Reference(TaggedSpec::class)) {
               executed = true
               Result.success(emptyMap())
            }

         executed.shouldBeTrue()
      }

      test("RequiresTagInterceptor should exclude spec if the tag expression does not contain the required tag") {

         val tags = TagExpression.Empty.include(NamedTag("UnrelatedTag"))
         val conf = ProjectConfiguration()
         conf.registry.add(SpecifiedTagsTagExtension(tags))
         val listener = spyk(NoopTestEngineListener)

         RequiresTagInterceptor(listener, conf, EmptyExtensionRegistry)
            .intercept(SpecRef.Reference(TaggedSpec::class)) {
               error("spec should not run")
            }

         coVerify { listener.specIgnored(TaggedSpec::class, "Disabled by @RequiresTag") }

      }

      test("RequiresTagInterceptor should exclude spec if the tag expression is blank") {

         val tags = TagExpression.Empty
         val conf = ProjectConfiguration()
         conf.registry.add(SpecifiedTagsTagExtension(tags))
         val listener = spyk(NoopTestEngineListener)

         RequiresTagInterceptor(listener, conf, EmptyExtensionRegistry)
            .intercept(SpecRef.Reference(TaggedSpec::class)) {
               error("spec should not run")
            }

         coVerify { listener.specIgnored(TaggedSpec::class, "Disabled by @RequiresTag") }
      }

   }
}

@io.kotest.core.annotation.Tags("SpecTagged")
@io.kotest.core.annotation.RequiresTag("SpecTagged")
private class TaggedSpec : ExpectSpec()
