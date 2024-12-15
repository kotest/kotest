package io.kotest.extensions.spring

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import org.springframework.test.context.TestExecutionListeners

@TestExecutionListeners(value = [SpringTestExecutionListener::class])
class SpringTestLifecycleModeTest : DescribeSpec() {
   init {

      extensions(SpringTestExtension(SpringTestLifecycleMode.Test))

      beforeSpec {
         before shouldBe 0
      }

      describe("an outer scope should not be initialized by the spring listener") {
         before shouldBe 0
         it("but the inner scope should be") {
            before shouldBe 1
            after shouldBe 0
         }
         after shouldBe 1
      }

      describe("an outer scope should not be initialized by the spring listener part 2") {
         before shouldBe 1
         it("but the inner scope should be") {
            before shouldBe 2
            after shouldBe 1
         }
         after shouldBe 2
      }

      afterSpec {
         after shouldBe 2
      }
   }
}

private var before = 0
private var after = 0

private class SpringTestExecutionListener : TestExecutionListener {

   override fun beforeTestMethod(testContext: TestContext) {
      before++
   }

   override fun afterTestMethod(testContext: TestContext) {
      after++
   }
}
