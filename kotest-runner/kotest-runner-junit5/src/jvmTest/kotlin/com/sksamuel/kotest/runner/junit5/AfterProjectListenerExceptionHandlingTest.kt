package com.sksamuel.kotest.runner.junit5

import io.kotest.core.config.configuration
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

@Isolate
class AfterProjectListenerExceptionHandlingTest : FunSpec({

   test("an AfterProjectListenerException should add marker test") {

      val ext = object : AfterProjectListener {
         override suspend fun afterProject() {
            error("bash!")
         }
      }

      configuration.register(ext)

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
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "foo",
               "After Project Error"
            )
         }

      configuration.deregister(ext)
   }

   test("multiple AfterProjectListenerException's should add multiple markers tests") {

      val ext1 = object : AfterProjectListener {
         override suspend fun afterProject() {
            error("whack")
         }
      }

      val ext2 = object : ProjectListener {
         override suspend fun afterProject() {
            error("zamm")
         }
      }

      configuration.register(ext1)
      configuration.register(ext2)

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
               "After Project Error_1",
               "After Project Error_2"
            )
            aborted().shouldBeEmpty()
            skipped().shouldBeEmpty()
            failed().shouldHaveNames("After Project Error_1", "After Project Error_2")
            succeeded().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "Kotest"
            )
            finished().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "After Project Error_1",
               "After Project Error_2",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "foo",
               "After Project Error_1",
               "After Project Error_2"
            )
         }

      configuration.deregister(ext1)
      configuration.deregister(ext2)
   }
})

private class AfterProjectListenerExceptionSample : FunSpec({
   test("foo") {}
})
