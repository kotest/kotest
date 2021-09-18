package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.config.configuration
import io.kotest.core.extensions.ProjectInterceptExtension
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.matchers.shouldBe

@Isolate
class ProjectExtensionEngineResultTest : FunSpec({

   val events = mutableListOf<String>()

   val extensions = listOf(
      object : ProjectInterceptExtension {
         val name = "hello q"
         override suspend fun interceptProject(callback: suspend () -> List<Throwable>): List<Throwable> {
            events.add(name)
            return callback()
         }
      },
      object : ProjectInterceptExtension {
         val name = "mon capitaine!"
         override suspend fun interceptProject(callback: suspend () -> List<Throwable>): List<Throwable> {
            callback();
            return listOf(ProjectExtensionThrowable(name))
         }
      },
   )

   beforeSpec {
      configuration.registerExtensions(extensions)
   }

   afterSpec {
      configuration.deregisterExtensions(extensions)
   }

   test("the test engine should return errors from project extensions in the engine result") {

      val errors = mutableListOf<Throwable>()

      val listener = object : TestEngineListener {
         override suspend fun engineFinished(t: List<Throwable>) {
            errors.addAll(t)
         }
      }

      KotestEngineLauncher().withListener(listener).withSpec(PassingProjectTest::class).launch()

      (events + errors.map { it.message }).toSet() shouldBe setOf("hello q", "mon capitaine!")
   }
})

private class ProjectExtensionThrowable(override val message: String) : Throwable()

private class PassingProjectTest : FunSpec({ test("a") { } })
