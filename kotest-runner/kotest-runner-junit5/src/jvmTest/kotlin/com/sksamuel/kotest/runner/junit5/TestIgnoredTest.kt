package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotCICondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit
import kotlin.streams.asSequence

@EnabledIf(NotCICondition::class)
class TestIgnoredTest : FunSpec() {
   init {
      test("should be notified of ignored tests") {
         val events = EngineTestKit
            .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
            .selectors(DiscoverySelectors.selectClass(IgnoreMe::class.java))
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents()
         // junit contact is that ignored tests should not be started or finished
         events.started()
            .map { it.testDescriptor.displayName }.asSequence().toSet().shouldNotContain("ignored")
         events.finished()
            .map { it.testDescriptor.displayName }.asSequence().toSet().shouldNotContain("ignored")

         // ignored test should be registered and parent should be set
         events.dynamicallyRegistered().filter { it.testDescriptor.displayName == "ignored" }
            .asSequence().first().testDescriptor.parent.isPresent shouldBe true
      }
   }
}

private class IgnoreMe : FunSpec() {
   init {
      xtest("ignored") {
         error("boom")
      }
   }
}
