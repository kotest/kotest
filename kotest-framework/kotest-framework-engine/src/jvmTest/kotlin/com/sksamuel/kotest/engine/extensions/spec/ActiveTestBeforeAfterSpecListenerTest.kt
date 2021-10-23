package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.config.Configuration
import io.kotest.core.extensions.TagExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.engine.spec.SpecExecutor
import io.kotest.matchers.shouldBe

@ExperimentalKotest
@Isolate
class ActiveTestBeforeAfterSpecListenerTest : FreeSpec() {
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
            val ext = object : TagExtension {
               override fun tags(): Tags = Tags("!bar")
            }
            val conf = Configuration()
            conf.registry().add(ext)
            conf.registry().add(testListener)
            val runner = SpecExecutor(NoopTestEngineListener, NoopCoroutineDispatcherFactory, conf)
            runner.execute(ReflectiveSpecRef(TaggedTests::class))
         }
         "by a tag at the spec level" {
            val ext = object : TagExtension {
               override fun tags(): Tags = Tags("!foo")
            }
            val conf = Configuration()
            conf.registry().add(ext)
            conf.registry().add(testListener)
            val runner = SpecExecutor(NoopTestEngineListener, NoopCoroutineDispatcherFactory, conf)
            runner.execute(ReflectiveSpecRef(TaggedTests::class))
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
