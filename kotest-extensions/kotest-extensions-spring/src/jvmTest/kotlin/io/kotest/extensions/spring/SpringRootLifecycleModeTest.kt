package io.kotest.extensions.spring

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import org.springframework.test.context.TestExecutionListeners

@TestExecutionListeners(value = [SpringRootExecutionListener::class])
class SpringRootLifecycleModeTest : DescribeSpec() {
   init {

      extension(SpringTestExtension(SpringTestLifecycleMode.Root))

      beforeSpec {
         before shouldBe 0
      }

      describe("the outer scope should be initialized by the spring listener") {
         before shouldBe 1
         it("inner scope should have no effect") {
            before shouldBe 1
            after shouldBe 0
         }
         after shouldBe 0
      }

      describe("the outer scope should be initialized by the spring listener part 2") {
         before shouldBe 2
         it("inner scope should have no effect") {
            before shouldBe 2
            after shouldBe 1
         }
         after shouldBe 1
      }

      afterSpec {
         after shouldBe 2
      }
   }
}

private var before = 0
private var after = 0

private class SpringRootExecutionListener : TestExecutionListener {

   override fun beforeTestMethod(testContext: TestContext) {
      before++
   }

   override fun afterTestMethod(testContext: TestContext) {
      after++
   }
}
