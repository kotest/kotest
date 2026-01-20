package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.project.ProjectContext
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.tags.TagExpression
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class ProjectExtensionEngineInterceptorTest : FunSpec({

   test("should invoke all extensions") {

      var fired1 = false
      var fired2 = false

      val ext1 = object : ProjectExtension {
         override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
            fired1 = true
            callback(context)
         }
      }

      val ext2 = object : ProjectExtension {
         override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
            fired2 = true
            callback(context)
         }
      }

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(ext1, ext2)
      }

      TestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withProjectConfig(c)
         .withSpecRefs(SpecRef.Reference(DummySpec2::class))
         .execute()

      fired1 shouldBe true
      fired2 shouldBe true
   }

   test("should invoke downstream after extensions") {

      var fired = false

      val ext1 = object : ProjectExtension {
         override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
            callback(context)
         }
      }

      val ext2 = object : ProjectExtension {
         override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
            callback(context)
         }
      }

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(ext1, ext2)
      }

      TestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withProjectConfig(c)
         .withSpecRefs(SpecRef.Reference(DummySpec2::class))
         .execute()

      fired shouldBe true
   }

   test("should invoke downstream without extensions") {

      var fired = false

      TestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withSpecRefs(SpecRef.Reference(DummySpec2::class))
         .execute()
      fired shouldBe true
   }

   test("should propagate tag changes") {
      var tags = TagExpression("none")

      val ext = object : ProjectExtension {
         override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
            callback(context.copy(tags = context.tags.include("bar")))
         }
      }

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(ext)
      }

      TestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withProjectConfig(c)
         .withSpecRefs(SpecRef.Reference(DummySpec2::class))
         .execute()

      tags.expression shouldBe "foo & bar"
   }
})

private class DummySpec2 : FunSpec({
   test("foo") {}
})
