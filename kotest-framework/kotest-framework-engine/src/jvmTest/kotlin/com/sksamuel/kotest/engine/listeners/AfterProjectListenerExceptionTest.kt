package com.sksamuel.kotest.engine.listeners

import io.kotest.core.config.configuration
import io.kotest.engine.lifecycle.AfterProjectListenerException
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf

@Isolate
class AfterProjectListenerExceptionTest : FunSpec({

   test("exception in afterProject should use AfterProjectListenerException") {

      val projectListener = object : ProjectListener {
         override suspend fun afterProject() {
            error("ARRGH")
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
         .withSpec(DummySpec7::class)
         .launch()

      errors shouldHaveSize 1
      errors[0].shouldBeInstanceOf<AfterProjectListenerException>()
      errors[0].cause!! shouldHaveMessage "ARRGH"

      configuration.deregisterListener(projectListener)
   }

   test("multiple afterProject exceptions should be collected") {

      val projectListener1 = object : ProjectListener {
         override suspend fun afterProject() {
            error("GLIPP")
         }
      }

      val projectListener2 = object : ProjectListener {
         override suspend fun afterProject() {
            error("WHACK")
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
         .withSpec(DummySpec7::class)
         .launch()

      errors shouldHaveSize 2
      errors.filterIsInstance<AfterProjectListenerException>() shouldHaveSize 2

      errors.forOne {
         it.cause!!.shouldHaveMessage("GLIPP")
      }

      errors.forOne {
         it.cause!!.shouldHaveMessage("WHACK")
      }

      configuration.deregisterListener(projectListener1)
      configuration.deregisterListener(projectListener2)
   }
})

private class DummySpec7 : FunSpec({
   test("foo") {}
})
