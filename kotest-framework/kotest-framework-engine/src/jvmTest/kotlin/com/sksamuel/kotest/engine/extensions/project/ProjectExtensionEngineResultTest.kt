package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.config.configuration
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.matchers.shouldBe

@Isolate
class ProjectExtensionEngineResultTest : FunSpec({

   val events = mutableListOf<String>()

   val extensions = listOf(
      object : ProjectExtension {
         val name = "hello q"
         override suspend fun interceptProject(callback: suspend () -> Unit) {
            events.add(name)
            return callback()
         }
      },
      object : ProjectExtension {
         val name = "mon capitaine!"
         override suspend fun interceptProject(callback: suspend () -> Unit) {
            throw ProjectExtensionThrowable(name)
         }
      },
   )

   beforeSpec {
      configuration.registerExtensions(extensions)
   }

   afterSpec {
      configuration.deregisterExtensions(extensions)
   }

   test("ProjectExtension errors should be propogated to the test engine") {

      val errors = mutableListOf<Throwable>()

      val listener = object : TestEngineListener {
         override suspend fun engineFinished(t: List<Throwable>) {
            errors.addAll(t)
         }
      }

      TestEngineLauncher(listener).withClasses(PassingProjectTest::class).launch()

      (events + errors.map { it.message }).toSet() shouldBe setOf("hello q", "mon capitaine!")
   }
})

private class ProjectExtensionThrowable(override val message: String) : Throwable()

private class PassingProjectTest : FunSpec({ test("a") { } })
