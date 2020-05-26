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
class BeforeAfterProjectListenerExceptionHandlingTest : FunSpec({

   test("BeforeProjectListenerException and AfterProjectListenerException should add marker spec") {
      mockkObject(Project)
      every { Project.listeners() } returns listOf(
         object : ProjectListener {
            override val name: String
               get() = "MyAfterProjectListenerName"
            override suspend fun afterProject() {
               if (System.getenv("foo") == "true") error("afterProjectError")
            }
         },
         object : ProjectListener {
            override val name: String
               get() = "MyBeforeProjectListenerName"
            override suspend fun beforeProject() {
               if (System.getenv("foo") == "true") error("beforeProjectError")
            }
         }
      )
      // use an env so that we only trigger the after all failure in the test, not while running the overall test suite
      withEnvironment("foo", "true") {
         EngineTestKit
            .engine("kotest")
            .selectors(DiscoverySelectors.selectClass(BeforeAfterProjectListenerExceptionSample::class.java))
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               started().shouldHaveNames(
                  "Kotest",
                  "MyBeforeProjectListenerName",
                  "MyAfterProjectListenerName"
               )
               aborted().shouldBeEmpty()
               skipped().shouldBeEmpty()
               failed().shouldHaveNames("MyBeforeProjectListenerName", "MyAfterProjectListenerName")
               succeeded().shouldHaveNames(
                  "Kotest"
               )
               finished().shouldHaveNames(
                  "MyBeforeProjectListenerName",
                  "MyAfterProjectListenerName",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "MyBeforeProjectListenerName",
                  "MyAfterProjectListenerName"
               )
            }
      }
      unmockkObject(Project)
   }
})

private class BeforeAfterProjectListenerExceptionSample : FunSpec({
   test("foo") {}
})

