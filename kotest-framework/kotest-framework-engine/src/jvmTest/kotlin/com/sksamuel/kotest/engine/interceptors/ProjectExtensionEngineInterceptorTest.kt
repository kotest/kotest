package com.sksamuel.kotest.engine.interceptors

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
         override suspend fun interceptProject(callback: suspend () -> Unit) {
            fired1 = true
            callback()
         }
      }

      val ext2 = object : ProjectExtension {
         override suspend fun interceptProject(callback: suspend () -> Unit) {
            fired2 = true
            callback()
         }
      }

      ProjectExtensionEngineInterceptor(listOf(ext1, ext2)).intercept(EngineContext.empty) { EngineResult.empty }

      fired1 shouldBe true
      fired2 shouldBe true
   }

   test("should invoke downstream after extensions") {

      var fired = false

      val ext1 = object : ProjectExtension {
         override suspend fun interceptProject(callback: suspend () -> Unit) {
            callback()
         }
      }

      val ext2 = object : ProjectExtension {
         override suspend fun interceptProject(callback: suspend () -> Unit) {
            callback()
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
})
