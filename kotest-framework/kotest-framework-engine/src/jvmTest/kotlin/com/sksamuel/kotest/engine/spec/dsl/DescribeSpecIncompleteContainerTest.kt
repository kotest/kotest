package com.sksamuel.kotest.engine.spec.dsl

import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestStatus
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

class DescribeSpecIncompleteContainerTest : FunSpec() {
   init {
      test("describe spec should error if not complete") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(IncompleteDescribeSpec::class)
            .launch()
         val desc = collector.tests.mapKeys { it.key.descriptor.id }
         desc[DescriptorId("foo")]?.status shouldBe TestStatus.Error
         desc[DescriptorId("foo")]?.error?.message shouldBe "Test 'foo' requires at least one nested test"
      }
   }
}

private class IncompleteDescribeSpec : DescribeSpec() {
   init {
      describe("foo") {}
   }
}
