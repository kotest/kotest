package com.sksamuel.kotest.engine.listeners

import io.kotest.core.config.configuration
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.BeforeProjectListenerException
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf

@Isolate
class BeforeProjectListenerExceptionTest : FunSpec({

   test("exception in beforeProject should use BeforeProjectListenerException") {

      val projectListener = object : ProjectListener {
         override suspend fun beforeProject() {
            error("OOOFF")
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
         .withSpec(DummySpec3::class)
         .launch()

      errors shouldHaveSize 1
      errors[0].shouldBeInstanceOf<BeforeProjectListenerException>()
      errors[0].cause!! shouldHaveMessage "OOOFF"

      configuration.deregisterListener(projectListener)
   }

   test("multiple beforeProject exceptions should be collected") {

      val projectListener1 = object : ProjectListener {
         override suspend fun beforeProject() {
            error("ZLOPP")
         }
      }

      val projectListener2 = object : ProjectListener {
         override suspend fun beforeProject() {
            error("WHAMM")
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
         .withSpec(DummySpec3::class)
         .launch()

      errors shouldHaveSize 2
      errors.filterIsInstance<BeforeProjectListenerException>() shouldHaveSize 2

      errors.forOne {
         it.cause!!.shouldHaveMessage("ZLOPP")
      }

      errors.forOne {
         it.cause!!.shouldHaveMessage("WHAMM")
      }

      configuration.deregisterListener(projectListener1)
      configuration.deregisterListener(projectListener2)
   }
})

private class DummySpec3 : FunSpec({
   test("foo") {}
})
