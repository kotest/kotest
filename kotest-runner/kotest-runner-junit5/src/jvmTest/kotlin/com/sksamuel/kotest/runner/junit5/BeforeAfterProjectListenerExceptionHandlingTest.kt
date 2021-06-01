package com.sksamuel.kotest.runner.junit5

import io.kotest.core.config.configuration
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.withSystemProperty
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

@Isolate
class BeforeAfterProjectListenerExceptionHandlingTest : FunSpec({

   test("BeforeProjectListener exception should add marker spec") {

      configuration.registerListener(
         object : ProjectListener {
            override val name: String
               get() = "wibble"

            override suspend fun beforeProject() {
               // use an env so that we only trigger the after all failure in the test, not while running the overall test suite
               if (System.getProperty("beforeProject") == "true") error("beforeProjectError")
            }
         }
      )

      // use an env so that we only trigger the after all failure in the test, not while running the overall test suite
      withSystemProperty("beforeProject", "true") {
         EngineTestKit
            .engine("kotest")
            .selectors(DiscoverySelectors.selectClass(BeforeAfterProjectListenerExceptionSample::class.java))
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               started().shouldHaveNames(
                  "Kotest",
                  "wibble",
               )
               aborted().shouldBeEmpty()
               skipped().shouldBeEmpty()
               failed().shouldHaveNames("wibble")
               succeeded().shouldHaveNames(
                  "Kotest"
               )
               finished().shouldHaveNames(
                  "wibble",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "wibble",
               )
            }
      }
   }

   test("AfterProjectListener exception should add marker spec") {

      configuration.registerListener(
         object : ProjectListener {
            override val name: String
               get() = "wobble"

            override suspend fun afterProject() {
               // use an env so that we only trigger the after all failure in the test, not while running the overall test suite
               if (System.getProperty("afterProject") == "true") error("afterProjectError")
            }
         }
      )

      // use an env so that we only trigger the after all failure in the test, not while running the overall test suite
      withSystemProperty("afterProject", "true") {
         EngineTestKit
            .engine("kotest")
            .selectors(DiscoverySelectors.selectClass(BeforeAfterProjectListenerExceptionSample::class.java))
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               started().shouldHaveNames(
                  "Kotest",
                  "com.sksamuel.kotest.runner.junit5.BeforeAfterProjectListenerExceptionSample",
                  "foo",
                  "wobble",
               )
               aborted().shouldBeEmpty()
               skipped().shouldBeEmpty()
               failed().shouldHaveNames("wobble")
               succeeded().shouldHaveNames(
                  "foo",
                  "com.sksamuel.kotest.runner.junit5.BeforeAfterProjectListenerExceptionSample",
                  "Kotest",
               )
               finished().shouldHaveNames(
                  "foo",
                  "com.sksamuel.kotest.runner.junit5.BeforeAfterProjectListenerExceptionSample",
                  "wobble",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "foo",
                  "wobble"
               )
            }
      }
   }
})

private class BeforeAfterProjectListenerExceptionSample : FunSpec({
   test("foo") {}
})

