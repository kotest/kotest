package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf

@Isolate
@EnabledIf(LinuxCondition::class)
class BeforeProjectListenerExceptionTest : FunSpec({

   test("exception in beforeProject should use BeforeProjectListenerException") {

      val errors: MutableList<Throwable> = mutableListOf()

      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineFinished(t: List<Throwable>) {
            errors.addAll(t)
         }
      }

      val c = ProjectConfiguration()
      c.registry.add(object : ProjectListener {
         override suspend fun beforeProject() {
            error("OOOFF")
         }
      })

      TestEngineLauncher(listener)
         .withClasses(DummySpec3::class)
         .withProjectConfig(c)
         .launch()

      errors shouldHaveSize 1
      errors[0].shouldBeInstanceOf<ExtensionException.BeforeProjectException>()
      errors[0].cause!! shouldHaveMessage "OOOFF"
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

      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineFinished(t: List<Throwable>) {
            errors.addAll(t)
         }
      }

      val c = ProjectConfiguration()
      c.registry.add(projectListener1)
      c.registry.add(projectListener2)

      TestEngineLauncher(listener)
         .withClasses(DummySpec3::class)
         .withProjectConfig(c)
         .launch()

      errors shouldHaveSize 2
      errors.filterIsInstance<ExtensionException.BeforeProjectException>() shouldHaveSize 2

      errors.forOne {
         it.cause!!.shouldHaveMessage("ZLOPP")
      }

      errors.forOne {
         it.cause!!.shouldHaveMessage("WHAMM")
      }
   }
})

private class DummySpec3 : FunSpec({
   test("foo") {}
})
