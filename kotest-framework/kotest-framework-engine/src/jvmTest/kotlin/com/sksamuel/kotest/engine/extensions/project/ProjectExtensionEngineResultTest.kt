package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.project.ProjectContext
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.matchers.shouldBe

@Isolate
@EnabledIf(LinuxOnlyGithubCondition::class)
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

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(ext1, ext2)
      }

      TestEngineLauncher(listener)
         .withClasses(PassingProjectTest::class)
         .withProjectConfig(c)
         .launch()

      (events + errors.map { it.message }).toSet() shouldBe setOf("hello q", "mon capitaine!")
   }
})

private class ProjectExtensionThrowable(override val message: String) : Throwable()

private class PassingProjectTest : FunSpec({ test("a") { } })
