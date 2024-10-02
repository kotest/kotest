package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

@Isolate
@EnabledIf(LinuxCondition::class)
class ProjectListenerExactlyOnceTest : WordSpec() {

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
      "Test Engine" should {
         "run beforeAll/afterAll once" {

            val c = ProjectConfiguration()
            c.registry.add(TestProjectListener)
            c.registry.add(TestBeforeProjectListener)

            TestEngineLauncher(NoopTestEngineListener)
               .withClasses(MyTest1::class, MyTest2::class)
               .withConfiguration(c)
               .launch()

            TestProjectListener.beforeAll shouldBe 1
            TestBeforeProjectListener.beforeAll shouldBe 1
            TestProjectListener.afterAll shouldBe 1
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
         ProjectListenerExactlyOnceTest.TestProjectListener.afterAll shouldBe 0
         ProjectListenerExactlyOnceTest.TestBeforeProjectListener.beforeAll shouldBe 1
      }
   }
}

private class MyTest2 : FunSpec() {
   init {
      test("checking beforeAll") {
         // we are asserting this in two places and it should be the same in both places
         ProjectListenerExactlyOnceTest.TestProjectListener.beforeAll shouldBe 1
         ProjectListenerExactlyOnceTest.TestBeforeProjectListener.beforeAll shouldBe 1
      }
      test("checking afterAll") {
         // this test spec has not yet completed, and therefore this count should be 0
         // we will also assert this in another test suite, where it should still be 0
         // but at that point at least _one_ test suite will have completed
         // so that will confirm it is not being fired after a spec has completed
         ProjectListenerExactlyOnceTest.TestProjectListener.afterAll shouldBe 0
         ProjectListenerExactlyOnceTest.TestBeforeProjectListener.beforeAll shouldBe 1
      }
   }
}
