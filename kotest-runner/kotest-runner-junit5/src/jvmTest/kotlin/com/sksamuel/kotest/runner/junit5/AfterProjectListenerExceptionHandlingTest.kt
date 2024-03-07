@file:Suppress("unused")

package com.sksamuel.kotest.runner.junit5

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.spec.style.FunSpec
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

class BashAfterProjectListener : AfterProjectListener {
   override suspend fun afterProject() {
      error("bash!")
   }
}

class WhackAfterProjectListener : AfterProjectListener {
   override suspend fun afterProject() {
      error("whack!")
   }
}

class AfterProjectListenerExceptionHandlingTest : FunSpec({

   test("an AfterProjectListenerException should add marker test") {
      EngineTestKit
         .engine("kotest")
         .selectors(DiscoverySelectors.selectClass(AfterProjectListenerExceptionSample::class.java))
         .configurationParameter("allow_private", "true")
         .configurationParameter("kotest.extensions", "com.sksamuel.kotest.runner.junit5.BashAfterProjectListener")
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "foo",
               "After Project Error",
            )
            aborted().shouldBeEmpty()
            skipped().shouldBeEmpty()
            failed().shouldHaveNames("After Project Error")
            succeeded().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "Kotest"
            )
            finished().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "After Project Error",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "foo",
               "After Project Error"
            )
         }
   }

   test("multiple AfterProjectListenerException's should add multiple markers tests") {
      EngineTestKit
         .engine("kotest")
         .selectors(DiscoverySelectors.selectClass(AfterProjectListenerExceptionSample::class.java))
         .configurationParameter("allow_private", "true")
         .configurationParameter(
            "kotest.extensions",
            "com.sksamuel.kotest.runner.junit5.BashAfterProjectListener,com.sksamuel.kotest.runner.junit5.WhackAfterProjectListener"
         )
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "foo",
               "After Project Error",
               "After Project Error_1"
            )
            aborted().shouldBeEmpty()
            skipped().shouldBeEmpty()
            failed().shouldHaveNames("After Project Error", "After Project Error_1")
            succeeded().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "Kotest"
            )
            finished().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "After Project Error",
               "After Project Error_1",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "foo",
               "After Project Error",
               "After Project Error_1"
            )
         }
   }
})

private class AfterProjectListenerExceptionSample : FunSpec({
   test("foo") {}
})
