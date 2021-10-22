package com.sksamuel.kotest.runner.junit5

import io.kotest.core.config.configuration
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

@Isolate
class BeforeProjectListenerExceptionHandlingTest : FunSpec({

   test("a BeforeProjectListenerException should add marker test using listener name") {

      val ext = object : ProjectListener {
         override suspend fun beforeProject() {
            error("beforeProjectError")
         }
      }

      configuration.register(ext)

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

      configuration.deregister(ext)
   }

   test("multiple BeforeProjectListenerException's should add multiple marker tests") {

      val ext1 = object : ProjectListener {

         override val name: String
            get() = "MyBeforeProjectListenerName1"

         override suspend fun beforeProject() {
            error("beforeProjectError1")
         }
      }

      val ext2 = object : ProjectListener {

         override val name: String
            get() = "MyBeforeProjectListenerName2"

         override suspend fun beforeProject() {
            error("beforeProjectError2")
         }
      }

      configuration.register(ext1)
      configuration.register(ext2)

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

      configuration.deregister(ext1)
      configuration.deregister(ext2)
   }
})

private class BeforeProjectListenerExceptionSample : FunSpec({
   test("foo") {}
})
