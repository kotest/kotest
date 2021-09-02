package com.sksamuel.kotest

import io.kotest.core.config.configuration
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

class ProjectListenerTest : WordSpec() {

   object TestProjectListener : ProjectListener {

      var beforeAll = 0
      var afterAll = 0

      override suspend fun beforeProject() {
         beforeAll++
      }

      override suspend fun afterProject() {
         afterAll++
      }
   }

   object TestBeforeProjectListener : BeforeProjectListener {

      var beforeAll = 0

      override suspend fun beforeProject() {
         beforeAll++
      }
   }

   init {
      "TestCase config" should {
         "run beforeAll/afterAll once" {

            configuration.registerListener(TestProjectListener)
            configuration.registerListener(TestBeforeProjectListener)

            KotestEngineLauncher()
               .withListener(NoopTestEngineListener)
               .withSpecs(listOf(MyTest1::class, MyTest2::class))
               .launch()

            TestProjectListener.beforeAll shouldBe 1
            TestBeforeProjectListener.beforeAll shouldBe 1
            TestProjectListener.afterAll shouldBe 1

            configuration.deregisterListener(TestProjectListener)
            configuration.deregisterListener(TestBeforeProjectListener)
         }
      }
   }
}

private class MyTest1 : FunSpec() {
   init {
      test("checking beforeAll") {
         // we are asserting this in two places, and it should be the same in both places

      }
      test("checking afterAll") {
         // this test spec has not yet completed, and therefore this count should be 0
         // we will also assert this in another test suite, where it should still be 0
         // but at that point at least _one_ test suite will have completed
         // so that will confirm it is not being fired after a spec has completed
         ProjectListenerTest.TestProjectListener.afterAll shouldBe 0
         ProjectListenerTest.TestBeforeProjectListener.beforeAll shouldBe 1
      }
   }
}

private class MyTest2 : FunSpec() {
   init {
      test("checking beforeAll") {
         // we are asserting this in two places and it should be the same in both places
         ProjectListenerTest.TestProjectListener.beforeAll shouldBe 1
         ProjectListenerTest.TestBeforeProjectListener.beforeAll shouldBe 1
      }
      test("checking afterAll") {
         // this test spec has not yet completed, and therefore this count should be 0
         // we will also assert this in another test suite, where it should still be 0
         // but at that point at least _one_ test suite will have completed
         // so that will confirm it is not being fired after a spec has completed
         ProjectListenerTest.TestProjectListener.afterAll shouldBe 0
         ProjectListenerTest.TestBeforeProjectListener.beforeAll shouldBe 1
      }
   }
}
