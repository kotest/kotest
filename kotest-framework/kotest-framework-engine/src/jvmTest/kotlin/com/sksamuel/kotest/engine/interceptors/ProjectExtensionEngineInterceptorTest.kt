package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.Tags
import io.kotest.core.extensions.ProjectContext
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.ProjectExtensionEngineInterceptor
import io.kotest.matchers.shouldBe

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

      ProjectExtensionEngineInterceptor(listOf(ext1, ext2)).intercept(EngineContext.empty) { EngineResult.empty }

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

      ProjectExtensionEngineInterceptor(listOf(ext1, ext2)).intercept(EngineContext.empty) {
         fired = true
         EngineResult.empty
      }

      fired shouldBe true
   }

   test("should invoke downstream without extensions") {

      var fired = false

      ProjectExtensionEngineInterceptor(emptyList()).intercept(EngineContext.empty) {
         fired = true
         EngineResult.empty
      }

      fired shouldBe true
   }

   test("should propagate tag changes") {
      var tags = Tags("none")

      val ext = object : ProjectExtension {
         override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
            callback(context.copy(tags = context.tags.include("bar")))
         }
      }

      ProjectExtensionEngineInterceptor(listOf(ext)).intercept(EngineContext.empty.withTags(Tags("foo"))) {
         tags = it.tags
         EngineResult.empty
      }

      tags.expression shouldBe "foo & bar"
   }
})
