package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.interceptors.ProjectExtensionEngineInterceptor
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

class ProjectExtensionEngineInterceptorTest : FunSpec({

   test("should invoke all extensions") {

      var fired1 = false
      var fired2 = false

      val ext1 = object : ProjectExtension {
         override suspend fun aroundProject(callback: suspend () -> List<Throwable>): List<Throwable> {
            fired1 = true
            return callback()
         }
      }

      val ext2 = object : ProjectExtension {
         override suspend fun aroundProject(callback: suspend () -> List<Throwable>): List<Throwable> {
            fired2 = true
            return callback()
         }
      }

      ProjectExtensionEngineInterceptor(listOf(ext1, ext2)).intercept(
         TestSuite.empty,
         NoopTestEngineListener
      ) { _, _ -> EngineResult.empty }

      fired1 shouldBe true
      fired2 shouldBe true
   }

   test("should invoke downstream after extensions") {

      var fired = false

      val ext1 = object : ProjectExtension {
         override suspend fun aroundProject(callback: suspend () -> List<Throwable>): List<Throwable> {
            return callback()
         }
      }

      val ext2 = object : ProjectExtension {
         override suspend fun aroundProject(callback: suspend () -> List<Throwable>): List<Throwable> {
            return callback()
         }
      }

      ProjectExtensionEngineInterceptor(listOf(ext1, ext2)).intercept(
         TestSuite.empty,
         NoopTestEngineListener
      ) { _, _ ->
         fired = true
         EngineResult.empty
      }

      fired shouldBe true
   }

   test("should accumulate errors") {

      val ext1 = object : ProjectExtension {
         override suspend fun aroundProject(callback: suspend () -> List<Throwable>): List<Throwable> {
            val errors = callback()
            return errors + RuntimeException("whack!")
         }
      }

      val ext2 = object : ProjectExtension {
         override suspend fun aroundProject(callback: suspend () -> List<Throwable>): List<Throwable> {
            val errors = callback()
            return errors + RuntimeException("zapp!")
         }
      }

      val result = ProjectExtensionEngineInterceptor(listOf(ext1, ext2)).intercept(
         TestSuite.empty,
         NoopTestEngineListener
      ) { _, _ ->
         EngineResult(listOf(RuntimeException("sock!")))
      }

      result.errors.size shouldBe 3
      result.errors.map { it.message }.toSet() shouldBe setOf("sock!", "zapp!", "whack!")
   }

   test("should invoke downstream without extensions") {

      var fired = false

      ProjectExtensionEngineInterceptor(emptyList()).intercept(
         TestSuite.empty,
         NoopTestEngineListener
      ) { _, _ ->
         fired = true
         EngineResult.empty
      }

      fired shouldBe true
   }
})
