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
class BeforeProjectListenerExceptionHandlingTest : FunSpec({

   beforeTest { mockkObject(Project) }
   afterTest { unmockkObject(Project) }

   test("an BeforeProjectListenerException should add marker spec") {
      every { Project.listeners() } returns listOf(
         object : ProjectListener {
            override suspend fun beforeProject() {
               if (System.getenv("foo") == "true") error("beforeProjectError")
            }
         }
      )
      // use an env so that we only trigger the after all failure in the test, not while running the overall test suite
      withEnvironment("foo", "true") {
         EngineTestKit
            .engine("kotest")
            .selectors(DiscoverySelectors.selectClass(BeforeProjectListenerExceptionSample::class.java))
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               started().shouldHaveNames(
                  "Kotest",
                  "defaultProjectListener"
               )
               aborted().shouldBeEmpty()
               skipped().shouldBeEmpty()
               failed().shouldHaveNames("defaultProjectListener")
               succeeded().shouldHaveNames(
                  "Kotest"
               )
               finished().shouldHaveNames(
                  "defaultProjectListener",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "defaultProjectListener"
               )
            }
      }
   }

   test("an BeforeProjectListener2Exception should add 2 markers spec") {
      every { Project.listeners() } returns listOf(
         object : ProjectListener {
            override val name: String
               get() = "MyBeforeProjectListenerName1"

            override suspend fun beforeProject() {
               if (System.getenv("foo") == "true") error("beforeProjectError")
            }
         },
         object : ProjectListener {
            override val name: String
               get() = "MyBeforeProjectListenerName2"

            override suspend fun beforeProject() {
               if (System.getenv("foo") == "true") error("beforeProjectError")
            }
         }
      )
      // use an env so that we only trigger the after all failure in the test, not while running the overall test suite
      withEnvironment("foo", "true") {
         EngineTestKit
            .engine("kotest")
            .selectors(DiscoverySelectors.selectClass(BeforeProjectListenerExceptionSample::class.java))
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               started().shouldHaveNames(
                  "Kotest",
                  "MyBeforeProjectListenerName1",
                  "MyBeforeProjectListenerName2"
               )
               aborted().shouldBeEmpty()
               skipped().shouldBeEmpty()
               failed().shouldHaveNames("MyBeforeProjectListenerName1", "MyBeforeProjectListenerName2")
               succeeded().shouldHaveNames(
                  "Kotest"
               )
               finished().shouldHaveNames(
                  "MyBeforeProjectListenerName1",
                  "MyBeforeProjectListenerName2",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "MyBeforeProjectListenerName1",
                  "MyBeforeProjectListenerName2"
               )
            }
      }
   }
})

private class BeforeProjectListenerExceptionSample : FunSpec({
   test("foo") {}
})
