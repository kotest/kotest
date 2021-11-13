package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.config.Configuration
import io.kotest.core.ProjectContext
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.matchers.shouldBe

@Isolate
class ProjectExtensionEngineResultTest : FunSpec({

   val events = mutableListOf<String>()

   val ext1 = object : ProjectExtension {
      val name = "hello q"
      override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
         events.add(name)
         return callback(context)
      }
   }
   val ext2 = object : ProjectExtension {
      val name = "mon capitaine!"
      override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
         throw ProjectExtensionThrowable(name)
      }
   }

   test("ProjectExtension errors should be propogated to the test engine") {

      val errors = mutableListOf<Throwable>()

      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineFinished(t: List<Throwable>) {
            errors.addAll(t)
         }
      }

      val c = Configuration()
      c.registry().add(ext1)
      c.registry().add(ext2)

      TestEngineLauncher(listener)
         .withClasses(PassingProjectTest::class)
         .withConfiguration(c)
         .launch()

      (events + errors.map { it.message }).toSet() shouldBe setOf("hello q", "mon capitaine!")
   }
})

private class ProjectExtensionThrowable(override val message: String) : Throwable()

private class PassingProjectTest : FunSpec({ test("a") { } })
