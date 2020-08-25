package com.sksamuel.kotest.core.runtime

import io.kotest.assertions.assertSoftly
import io.kotest.core.config.configuration
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.AfterProjectListenerException
import io.kotest.core.listeners.BeforeProjectListenerException
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize

@DoNotParallelize
class BeforeAfterProjectTest : FunSpec({

   test("2 errors from failed beforeProject and AfterProject listeners should be collected") {

      val projectListener1 = object : ProjectListener {
         override suspend fun beforeProject() {
            error("boom")
         }
      }

      val projectListener2 = object : ProjectListener {
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

      configuration.registerListener(projectListener1)
      configuration.registerListener(projectListener2)
      KotestEngineLauncher()
         .withListener(listener)
         .withSpec(DummySpec::class)
         .launch()
      assertSoftly {
         errors shouldHaveSize 2
         errors.filterIsInstance<BeforeProjectListenerException>() shouldHaveSize 1
         errors.filterIsInstance<AfterProjectListenerException>() shouldHaveSize 1
      }
      configuration.deregisterListener(projectListener1)
      configuration.deregisterListener(projectListener2)
   }
})

private class DummySpec : FunSpec({
   test("foo") {}
})
