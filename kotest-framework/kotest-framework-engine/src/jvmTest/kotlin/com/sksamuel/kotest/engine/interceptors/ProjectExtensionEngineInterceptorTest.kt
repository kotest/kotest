package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.project.ProjectContext
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.ProjectExtensionEngineInterceptor
import io.kotest.engine.tags.TagExpression
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
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

      ProjectExtensionEngineInterceptor.intercept(EngineContext.empty.withProjectConfig(c)) { EngineResult.empty }

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

      ProjectExtensionEngineInterceptor.intercept(EngineContext.empty.withProjectConfig(c)) {
         fired = true
         EngineResult.empty
      }

      fired shouldBe true
   }

   test("should invoke downstream without extensions") {

      var fired = false

      ProjectExtensionEngineInterceptor.intercept(EngineContext.empty) {
         fired = true
         EngineResult.empty
      }

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

      ProjectExtensionEngineInterceptor.intercept(EngineContext.empty.withTags(TagExpression("foo")).withProjectConfig(c)) {
         tags = it.tags
         EngineResult.empty
      }

      tags.expression shouldBe "foo & bar"
   }
})
