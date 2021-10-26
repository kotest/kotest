package com.sksamuel.kotest.runner.junit5

import io.kotest.core.config.configuration
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

@Isolate
class BeforeProjectListenerExceptionHandlingTest : FunSpec({

   test("!a BeforeProjectListenerException should add marker test using listener name") {

      val ext = object : ProjectListener {
         override suspend fun beforeProject() {
            error("zamm!")
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
               "Before Project Error"
            )
            aborted().shouldBeEmpty()
            skipped().shouldBeEmpty()
            failed().shouldHaveNames("Before Project Error")
            succeeded().shouldHaveNames(
               "Kotest"
            )
            finished().shouldHaveNames(
               "Before Project Error",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "Before Project Error"
            )
         }

      configuration.deregister(ext)
   }

   test("!multiple BeforeProjectListenerException's should add multiple marker tests") {

      val ext1 = object : ProjectListener {
         override suspend fun beforeProject() {
            error("clopp!")
         }
      }

      val ext2 = object : ProjectListener {
         override suspend fun beforeProject() {
            error("whack!")
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
               "Before Project Error",
               "Before Project Error_1"
            )
            aborted().shouldBeEmpty()
            skipped().shouldBeEmpty()
            failed().shouldHaveNames("Before Project Error", "Before Project Error_1")
            succeeded().shouldHaveNames(
               "Kotest"
            )
            finished().shouldHaveNames(
               "Before Project Error",
               "Before Project Error_1",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "Before Project Error",
               "Before Project Error_1"
            )
         }

      configuration.deregister(ext1)
      configuration.deregister(ext2)
   }
})

private class BeforeProjectListenerExceptionSample : FunSpec({
   test("foo") {}
})
