package io.kotest

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

private class FooTest : DescribeSpec({
   describe("a property test") {
      val myList = listOf(1, 2, 3)

      checkAll(myList.exhaustive()) {
         it("Should log for $it") {
            it shouldBe 1
         }
      }
   }

   describe("a non property test") {
      it("a dummy test with no assertion") {

      }

      it("a dummy test with two assertions both of which will fail") {
         1 shouldBe 2
         3 shouldBe 4
      }
   }

})

class GlobalAssertionEnableBehaviourTest : FunSpec({
   test("globalAssertSoftly should work for property test and non property test") {
      val collector = CollectingTestEngineListener()
      val projectConfiguration = ProjectConfiguration()
      projectConfiguration.globalAssertSoftly = true

      TestEngineLauncher(collector, projectConfiguration, emptyList(), emptyList(), null)
         .withClasses(FooTest::class)
         .launch()

      collector.result("Should log for 1")?.isSuccess shouldBe true
      collector.result("Should log for 2")?.isFailure shouldBe true
      collector.result("Should log for 3")?.isFailure shouldBe true
   }

   test("globalAssertSoftly should work for non property test") {
      val collector = CollectingTestEngineListener()
      val projectConfiguration = ProjectConfiguration()
      projectConfiguration.globalAssertSoftly = true

      TestEngineLauncher(collector, projectConfiguration, emptyList(), emptyList(), null)
         .withClasses(FooTest::class)
         .launch()

      collector.result("a dummy test with no assertion")?.isSuccess shouldBe true
      collector.result("a dummy test with two assertions both of which will fail")?.isFailure shouldBe true
   }
})
