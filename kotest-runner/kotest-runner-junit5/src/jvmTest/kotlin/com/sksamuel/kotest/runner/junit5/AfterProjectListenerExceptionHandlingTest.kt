package com.sksamuel.kotest.runner.junit5

import io.kotest.core.config.configuration
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

@Isolate
class AfterProjectListenerExceptionHandlingTest : FunSpec({

   beforeTest { mockkObject(configuration) }
   afterTest { unmockkObject(configuration) }

   test("an AfterProjectListenerException should add marker test") {

      val ext = object : AfterProjectListener {

         override val name: String
            get() = "myAfterProjectListener"

         override suspend fun afterProject() {
            error("afterProjectException")
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
               "myAfterProjectListener",
            )
            aborted().shouldBeEmpty()
            skipped().shouldBeEmpty()
            failed().shouldHaveNames("myAfterProjectListener")
            succeeded().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "Kotest"
            )
            finished().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "myAfterProjectListener",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "foo",
               "myAfterProjectListener"
            )
         }

      configuration.deregister(ext)
   }

   test("multiple AfterProjectListenerException's should add multiple markers tests") {

      val ext1 = object : AfterProjectListener {

         override val name: String
            get() = "myAfterProjectListener1"

         override suspend fun afterProject() {
            error("whack")
         }
      }

      val ext2 = object : ProjectListener {

         override val name: String
            get() = "myAfterProjectListener2"

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
               "myAfterProjectListener1",
               "myAfterProjectListener2"
            )
            aborted().shouldBeEmpty()
            skipped().shouldBeEmpty()
            failed().shouldHaveNames("myAfterProjectListener1", "myAfterProjectListener2")
            succeeded().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "Kotest"
            )
            finished().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "myAfterProjectListener1",
               "myAfterProjectListener2",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "foo",
               "myAfterProjectListener1",
               "myAfterProjectListener2"
            )
         }

      configuration.deregister(ext1)
      configuration.deregister(ext2)
   }

   test("multiple AfterProjectListenerException's should disambiguate names") {

      val ext1 = object : ProjectListener {

         override val name: String
            get() = "goo"

         override suspend fun afterProject() {
            error("whack")
         }
      }

      val ext2 = object : ProjectListener {

         override val name: String
            get() = "goo"

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
               "goo",
               "(1) goo"
            )
            aborted().shouldBeEmpty()
            skipped().shouldBeEmpty()
            failed().shouldHaveNames("goo", "(1) goo")
            succeeded().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "Kotest"
            )
            finished().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "goo",
               "(1) goo",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "foo",
               "goo",
               "(1) goo"
            )
         }
   }
})

private class AfterProjectListenerExceptionSample : FunSpec({
   test("foo") {}
})
