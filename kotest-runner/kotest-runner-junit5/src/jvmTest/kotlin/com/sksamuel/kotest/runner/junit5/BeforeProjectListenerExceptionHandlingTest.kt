package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

class ZammBeforeProjectListener : ProjectListener {
   override suspend fun beforeProject() {
      error("zamm!")
   }
}

class WhackBeforeProjectListener : ProjectListener {
   override suspend fun beforeProject() {
      error("whack!")
   }
}

@EnabledIf(LinuxCondition::class)
class BeforeProjectListenerExceptionHandlingTest : FunSpec({

   test("a BeforeProjectListenerException should add marker test using listener name") {

      EngineTestKit
         .engine("kotest")
         .selectors(DiscoverySelectors.selectClass(BeforeProjectListenerExceptionSample::class.java))
         .configurationParameter("allow_private", "true")
         .configurationParameter("kotest.extensions", "com.sksamuel.kotest.runner.junit5.ZammBeforeProjectListener")
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
   }

   test("multiple BeforeProjectListenerException's should add multiple marker tests") {

      EngineTestKit
         .engine("kotest")
         .selectors(DiscoverySelectors.selectClass(BeforeProjectListenerExceptionSample::class.java))
         .configurationParameter("allow_private", "true")
         .configurationParameter(
            "kotest.extensions",
            "com.sksamuel.kotest.runner.junit5.ZammBeforeProjectListener,com.sksamuel.kotest.runner.junit5.WhackBeforeProjectListener"
         )
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
   }
})

private class BeforeProjectListenerExceptionSample : FunSpec({
   test("foo") {}
})
