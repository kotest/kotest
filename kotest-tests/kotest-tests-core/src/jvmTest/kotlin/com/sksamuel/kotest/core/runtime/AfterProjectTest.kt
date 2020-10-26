package com.sksamuel.kotest.core.runtime

import io.kotest.assertions.assertSoftly
import io.kotest.core.config.configuration
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.AfterProjectListenerException
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf

@DoNotParallelize
class AfterProjectTest : FunSpec({

   test("after project error should use AfterAllListenerException") {

      val projectListener = object : ProjectListener {
         override suspend fun afterProject() {
            error("boom")
         }
      }

      val errors: MutableList<Throwable> = mutableListOf()

      val listener = object : TestEngineListener {
         override fun engineFinished(t: List<Throwable>) {
            errors.addAll(t)
         }
      }

      configuration.registerListener(projectListener)
      KotestEngineLauncher()
         .withListener(listener)
         .withSpec(DummySpec2::class)
         .launch()
      assertSoftly {
         errors shouldHaveSize 1
         errors[0].shouldBeInstanceOf<AfterProjectListenerException>()
      }
      configuration.deregisterListener(projectListener)
   }

   test("after project errors should have size 2") {

      val projectListener1 = object : ProjectListener {
         override suspend fun afterProject() {
            error("boom1")
         }
      }

      val projectListener2 = object : ProjectListener {
         override suspend fun afterProject() {
            error("boom2")
         }
      }

      val errors: MutableList<Throwable> = mutableListOf()

      val listener = object : TestEngineListener {
         override fun engineFinished(t: List<Throwable>) {
            errors.addAll(t)
         }
      }

      configuration.registerListener(projectListener1)
      configuration.registerListener(projectListener2)
      KotestEngineLauncher()
         .withListener(listener)
         .withSpec(DummySpec2::class)
         .launch()
      assertSoftly {
         errors shouldHaveSize 2
         errors.filterIsInstance<AfterProjectListenerException>() shouldHaveSize 2
      }
      configuration.deregisterListener(projectListener1)
      configuration.deregisterListener(projectListener2)
   }
})

private class DummySpec2 : FunSpec({
   test("foo") {}
})
