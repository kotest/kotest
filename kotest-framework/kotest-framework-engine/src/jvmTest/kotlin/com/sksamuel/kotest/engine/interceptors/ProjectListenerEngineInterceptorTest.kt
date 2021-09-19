package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.events.AfterProjectListenerException
import io.kotest.engine.events.BeforeProjectListenerException
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.ProjectListenerEngineInterceptor
import io.kotest.matchers.shouldBe

class ProjectListenerEngineInterceptorTest : FunSpec({

   test("should invoke beforeProject listener") {
      var fired = false
      val listener = object : BeforeProjectListener {
         override suspend fun beforeProject() {
            fired = true
         }
      }
      ProjectListenerEngineInterceptor(listOf(listener)).intercept(EngineContext.empty) { EngineResult(emptyList()) }

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
      ProjectListenerEngineInterceptor(listOf(listener1, listener2)).intercept(
         EngineContext.empty
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
      ProjectListenerEngineInterceptor(listOf(listener)).intercept(
         EngineContext.empty
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
      ProjectListenerEngineInterceptor(listOf(listener1, listener2)).intercept(
         EngineContext.empty
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
      val results = ProjectListenerEngineInterceptor(listOf(listener1, listener2)).intercept(
         EngineContext.empty
      ) { EngineResult(emptyList()) }
      results.errors.filterIsInstance<BeforeProjectListenerException>().size shouldBe 2
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
      val results = ProjectListenerEngineInterceptor(listOf(listener1, listener2)).intercept(
         EngineContext.empty
      ) { EngineResult(emptyList()) }
      results.errors.filterIsInstance<AfterProjectListenerException>().size shouldBe 2
   }
})
