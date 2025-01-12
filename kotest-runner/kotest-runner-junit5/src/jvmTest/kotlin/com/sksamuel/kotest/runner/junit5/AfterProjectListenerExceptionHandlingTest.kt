@file:Suppress("unused")

package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
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

@EnabledIf(LinuxCondition::class)
class AfterProjectListenerExceptionHandlingTest : FunSpec({

   test("an AfterProjectListenerException should add marker test") {
      EngineTestKit
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(DiscoverySelectors.selectClass(AfterProjectListenerExceptionSample::class.java))
         .configurationParameter("allow_private", "true")
         .configurationParameter("kotest.extensions", "com.sksamuel.kotest.runner.junit5.BashAfterProjectListener")
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
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
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            finished().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "After Project Error",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            dynamicallyRegistered().shouldHaveNames(
               "foo",
               "After Project Error"
            )
         }
   }

   test("multiple AfterProjectListenerException's should add multiple markers tests") {
      EngineTestKit
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(DiscoverySelectors.selectClass(AfterProjectListenerExceptionSample::class.java))
         .configurationParameter("allow_private", "true")
         .configurationParameter(
            "kotest.extensions",
            "com.sksamuel.kotest.runner.junit5.BashAfterProjectListener,com.sksamuel.kotest.runner.junit5.WhackAfterProjectListener"
         )
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
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
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            finished().shouldHaveNames(
               "foo",
               "com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionSample",
               "After Project Error",
               "After Project Error_1",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
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
