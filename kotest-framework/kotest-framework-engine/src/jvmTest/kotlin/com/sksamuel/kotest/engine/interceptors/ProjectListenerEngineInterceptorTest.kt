package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.ProjectListenerEngineInterceptor
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class ProjectListenerEngineInterceptorTest : FunSpec({

   test("should invoke beforeProject listener") {
      var fired = false
      val listener = object : BeforeProjectListener {
         override suspend fun beforeProject() {
            fired = true
         }
      }
      val c = object : AbstractProjectConfig() {
         override fun extensions() = listOf(listener)
      }

      ProjectListenerEngineInterceptor.intercept(
         EngineContext.empty.withProjectConfig(c)
      ) { EngineResult(emptyList()) }

      fired shouldBe true
   }

   test("should invoke multiple beforeProject listeners") {
      var fired1 = false
      var fired2 = false
      val listener1 = object : BeforeProjectListener {
         override suspend fun beforeProject() {
            fired1 = true
         }
      }
      val listener2 = object : BeforeProjectListener {
         override suspend fun beforeProject() {
            fired2 = true
         }
      }
      val c = object : AbstractProjectConfig() {
         override fun extensions() = listOf(listener1, listener2)
      }
      ProjectListenerEngineInterceptor.intercept(
         EngineContext.empty.withProjectConfig(c)
      ) { EngineResult(emptyList()) }

      fired1 shouldBe true
      fired2 shouldBe true
   }

   test("should invoke afterProject listeners") {
      var fired = false
      val listener = object : AfterProjectListener {
         override suspend fun afterProject() {
            fired = true
         }
      }
      val c = object : AbstractProjectConfig() {
         override fun extensions() = listOf(listener)
      }
      ProjectListenerEngineInterceptor.intercept(
         EngineContext.empty.withProjectConfig(c)
      ) { EngineResult(emptyList()) }

      fired shouldBe true
   }

   test("should invoke multiple afterProject listeners") {
      var fired1 = false
      var fired2 = false
      val listener1 = object : AfterProjectListener {
         override suspend fun afterProject() {
            fired1 = true
         }
      }
      val listener2 = object : AfterProjectListener {
         override suspend fun afterProject() {
            fired2 = true
         }
      }
      val c = object : AbstractProjectConfig() {
         override fun extensions() = listOf(listener1, listener2)
      }
      ProjectListenerEngineInterceptor.intercept(
         EngineContext.empty.withProjectConfig(c)
      ) { EngineResult(emptyList()) }

      fired1 shouldBe true
      fired2 shouldBe true
   }

   test("should return BeforeProjectListener errors wrapped in BeforeProjectListenerException") {
      val listener1 = object : BeforeProjectListener {
         override suspend fun beforeProject() {
            error("whack!")
         }
      }
      val listener2 = object : BeforeProjectListener {
         override suspend fun beforeProject() {
            error("zapp!")
         }
      }
      val c = object : AbstractProjectConfig() {
         override fun extensions() = listOf(listener1, listener2)
      }
      val results = ProjectListenerEngineInterceptor.intercept(
         EngineContext.empty.withProjectConfig(c)
      ) { EngineResult(emptyList()) }
      results.errors.filterIsInstance<ExtensionException.BeforeProjectException>().size shouldBe 2
   }

   test("should return AfterProjectListener errors wrapped in AfterProjectListenerException") {
      val listener1 = object : AfterProjectListener {
         override suspend fun afterProject() {
            error("whack!")
         }
      }
      val listener2 = object : AfterProjectListener {
         override suspend fun afterProject() {
            error("zapp!")
         }
      }
      val c = object : AbstractProjectConfig() {
         override fun extensions() = listOf(listener1, listener2)
      }
      val results = ProjectListenerEngineInterceptor.intercept(
         EngineContext.empty.withProjectConfig(c)
      ) { EngineResult(emptyList()) }
      results.errors.filterIsInstance<ExtensionException.AfterProjectException>().size shouldBe 2
   }
})
