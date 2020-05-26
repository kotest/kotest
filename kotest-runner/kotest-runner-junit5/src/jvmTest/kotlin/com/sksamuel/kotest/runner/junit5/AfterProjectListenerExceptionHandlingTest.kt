package com.sksamuel.kotest.runner.junit5

import io.kotest.core.config.Project
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.withEnvironment
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

@DoNotParallelize
class AfterProjectListenerExceptionHandlingTest : FunSpec({

   beforeTest { mockkObject(Project) }
   afterTest { unmockkObject(Project) }

   test("an AfterProjectListenerException should add marker spec") {
      every { Project.listeners() } returns listOf(
         object : ProjectListener {
            override suspend fun afterProject() {
               if (System.getenv("foo") == "true") error("too")
            }
         }
      )
      // use an env so that we only trigger the after all failure in the test, not while running the overall test suite
      withEnvironment("foo", "true") {
         EngineTestKit
            .engine("kotest")
            .selectors(DiscoverySelectors.selectClass(AfterProjectListenerExceptionSample::class.java))
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               started().shouldHaveNames(
                  "Kotest",
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "foo",
                  "defaultProjectListener"
               )
               aborted().shouldBeEmpty()
               skipped().shouldBeEmpty()
               failed().shouldHaveNames("defaultProjectListener")
               succeeded().shouldHaveNames(
                  "foo",
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "Kotest"
               )
               finished().shouldHaveNames(
                  "foo",
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "defaultProjectListener",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "foo",
                  "defaultProjectListener"
               )
            }
      }
   }

   test("an AfterProjectListenerException should add 2 markers spec") {
      every { Project.listeners() } returns listOf(
         object : ProjectListener {
            override suspend fun afterProject() {
               if (System.getenv("coo") == "true") error("moo")
            }
         },
         object : ProjectListener {
            override suspend fun afterProject() {
               if (System.getenv("coo") == "true") error("boo")
            }
         }
      )
      // use an env so that we only trigger the after all failure in the test, not while running the overall test suite
      withEnvironment("coo", "true") {
         EngineTestKit
            .engine("kotest")
            .selectors(DiscoverySelectors.selectClass(AfterProjectListenerExceptionSample::class.java))
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               started().shouldHaveNames(
                  "Kotest",
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "foo",
                  "defaultProjectListener_0",
                  "defaultProjectListener_1"
               )
               aborted().shouldBeEmpty()
               skipped().shouldBeEmpty()
               failed().shouldHaveNames("defaultProjectListener_0", "defaultProjectListener_1")
               succeeded().shouldHaveNames(
                  "foo",
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "Kotest"
               )
               finished().shouldHaveNames(
                  "foo",
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "defaultProjectListener_0",
                  "defaultProjectListener_1",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "foo",
                  "defaultProjectListener_0",
                  "defaultProjectListener_1"
               )
            }
      }
   }

   test("an AfterProjectListenerException should add named markers spec") {
      every { Project.listeners() } returns listOf(
         object : ProjectListener {
            override val name: String
               get() = "MyAfterProjectListenerName"

            override suspend fun afterProject() {
               if (System.getenv("foo") == "true") error("moo")
            }
         }
      )
      // use an env so that we only trigger the after all failure in the test, not while running the overall test suite
      withEnvironment("foo", "true") {
         EngineTestKit
            .engine("kotest")
            .selectors(DiscoverySelectors.selectClass(AfterProjectListenerExceptionSample::class.java))
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               started().shouldHaveNames(
                  "Kotest",
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "foo",
                  "MyAfterProjectListenerName"
               )
               aborted().shouldBeEmpty()
               skipped().shouldBeEmpty()
               failed().shouldHaveNames("MyAfterProjectListenerName")
               succeeded().shouldHaveNames(
                  "foo",
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "Kotest"
               )
               finished().shouldHaveNames(
                  "foo",
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "MyAfterProjectListenerName",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "foo",
                  "MyAfterProjectListenerName"
               )
            }
      }
   }
})

private class AfterProjectListenerExceptionSample : FunSpec({
   test("foo") {}
})
