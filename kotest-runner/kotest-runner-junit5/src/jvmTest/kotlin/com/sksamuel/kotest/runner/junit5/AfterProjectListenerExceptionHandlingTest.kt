package com.sksamuel.kotest.runner.junit5

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.withEnvironment
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

class AfterProjectListenerExceptionHandlingTest : FunSpec({

   test("an AfterProjectListenerException should add marker spec") {
      // use an env so that we only trigger the after all failure in the test, not while running the overall test suite
      withEnvironment("foo", "true") {
         EngineTestKit
            .engine("kotest")
            .selectors(DiscoverySelectors.selectClass(AfterProjectListenerExceptionSample::class.java))
            .configurationParameter("allow_internal", "true")
            .execute()
            .allEvents().apply {
               started().shouldHaveNames(
                  "Kotest",
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "foo",
                  "AfterAllCallback"
               )
               aborted().shouldBeEmpty()
               skipped().shouldBeEmpty()
               failed().shouldHaveNames("AfterAllCallback")
               succeeded().shouldHaveNames(
                  "foo",
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "Kotest"
               )
               finished().shouldHaveNames(
                  "foo",
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
                  "AfterAllCallback",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample", "foo", "AfterAllCallback"
               )
            }
      }
   }
})

internal class AfterProjectListenerExceptionSample : FunSpec({
   afterProject { if (System.getenv("foo") == "true") error("foo") }
   test("foo") {}
})
