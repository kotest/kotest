package com.sksamuel.kotest.tags

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.engine.spec.SpecExecutor
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.extensions.TagExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@DoNotParallelize
class ActiveTestSpecCallbackTest : FreeSpec() {
   init {

      var error = false

      val testListener = object : TestListener {
         override suspend fun beforeSpec(spec: Spec) {
            error = true
         }

         override suspend fun afterSpec(spec: Spec) {
            error = true
         }
      }

      afterSpec {
         error shouldBe false
      }

      "beforeSpec and afterSpec should not fire when all root tests are filtered out" - {
         "by a tag on the test itself" {
            val listener = object : TestEngineListener {}
            val ext = object : TagExtension {
               override fun tags(): Tags = Tags("!bar")
            }
            configuration.registerExtension(ext)
            configuration.registerListener(testListener)
            val runner = SpecExecutor(listener)
            runner.execute(TaggedTests::class)
            configuration.deregisterListener(testListener)
            configuration.deregisterExtension(ext)
         }
         "by a tag at the spec level" {
            val listener = object : TestEngineListener {}
            val ext = object : TagExtension {
               override fun tags(): Tags = Tags("!foo")
            }
            configuration.registerExtension(ext)
            configuration.registerListener(testListener)
            val runner = SpecExecutor(listener)
            runner.execute(TaggedTests::class)
            configuration.deregisterListener(testListener)
            configuration.deregisterExtension(ext)
         }
      }
   }
}

val bar = NamedTag("bar")
val foo = NamedTag("bar")

@io.kotest.core.annotation.Tags("foo")
private class TaggedTests : FunSpec() {

   override fun tags(): Set<Tag> = setOf(foo)

   init {
      beforeSpec {
         error("boom")
      }
      afterSpec {
         error("boom")
      }
      test("this test should be disabled").config(tags = setOf(bar)) {}
   }
}
