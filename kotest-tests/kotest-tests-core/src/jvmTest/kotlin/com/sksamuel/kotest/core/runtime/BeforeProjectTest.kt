package com.sksamuel.kotest.core.runtime

import io.kotest.assertions.assertSoftly
import io.kotest.core.config.Project
import io.kotest.core.engine.KotestEngineLauncher
import io.kotest.core.engine.TestEngineListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.runtime.BeforeProjectListenerException
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf

@DoNotParallelize
class BeforeProjectTest : FunSpec({

   test("beforeProject error should use BeforeProjectListenerException") {

      val projectListener = object : ProjectListener {
         override val name
            get() = "BeforeAllTest ProjectListener"

         override suspend fun beforeProject() {
            error("boom")
         }
      }

      val errors: MutableList<Throwable> = mutableListOf()

      val listener = object : TestEngineListener {
         override fun engineFinished(t: List<Throwable>) {
            errors.addAll(t)
         }
      }

      Project.registerListener(projectListener)
      KotestEngineLauncher(listener).forSpec(DummySpec3::class).launch()
      assertSoftly {
         errors shouldHaveSize 1
         errors[0].shouldBeInstanceOf<BeforeProjectListenerException>()
      }
      Project.deregisterListener(projectListener)
   }

   test("2 failed beforeProject listener should be collected") {

      val projectListener1 = object : ProjectListener {
         override val name
            get() = "BeforeAllTest1 ProjectListener"

         override suspend fun beforeProject() {
            error("boom")
         }
      }

      val projectListener2 = object : ProjectListener {
         override val name
            get() = "BeforeAllTest2 ProjectListener"

         override suspend fun beforeProject() {
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
      KotestEngineLauncher(listener).forSpec(DummySpec3::class).launch()
      assertSoftly {
         errors shouldHaveSize 2
         errors.filterIsInstance<BeforeProjectListenerException>() shouldHaveSize 2
      }
      Project.deregisterListener(projectListener1)
      Project.deregisterListener(projectListener2)
   }
})

private class DummySpec3 : FunSpec({
   test("foo") {}
})
