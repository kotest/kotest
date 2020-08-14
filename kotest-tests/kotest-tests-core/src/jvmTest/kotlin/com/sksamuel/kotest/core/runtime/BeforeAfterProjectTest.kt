package com.sksamuel.kotest.core.runtime

import io.kotest.assertions.assertSoftly
import io.kotest.engine.config.Project
import io.kotest.engine.launcher.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.engine.callbacks.AfterProjectListenerException
import io.kotest.engine.callbacks.BeforeProjectListenerException
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

      Project.registerListener(projectListener1)
      Project.registerListener(projectListener2)
      KotestEngineLauncher(listener).forSpec(DummySpec::class).launch()
      assertSoftly {
         errors shouldHaveSize 2
         errors.filterIsInstance<BeforeProjectListenerException>() shouldHaveSize 1
         errors.filterIsInstance<AfterProjectListenerException>() shouldHaveSize 1
      }
      Project.deregisterListener(projectListener1)
      Project.deregisterListener(projectListener2)
   }
})

private class DummySpec : FunSpec({
   test("foo") {}
})
