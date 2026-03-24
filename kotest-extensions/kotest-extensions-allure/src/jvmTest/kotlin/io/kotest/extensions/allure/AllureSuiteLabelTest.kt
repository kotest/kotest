package io.kotest.extensions.allure

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import io.qameta.allure.util.ResultsUtils

/**
 * Verifies Allure suite label behavior driven by [JVM_SUITE_NAME].
 *
 * When [JVM_SUITE_NAME] is absent the suite label falls back to the spec's fully qualified
 * class name (existing behavior).  When it is present, the suite label becomes the suite name
 * and the spec FQN is placed in the sub-suite slot, giving a two-level hierarchy in the Allure
 * report.
 */
class AllureSuiteLabelTest : FunSpec() {

   // Use a custom AllureWriter that returns null – simulates no JVM_SUITE_NAME env var.
   private val writerNoSuite = AllureWriter(null)
   private val reporterNoSuite = AllureTestReporter(writer = writerNoSuite)

   // Use a custom AllureWriter that returns a fixed suite name.
   private val writerWithSuite = AllureWriter("integrationTest")
   private val reporterWithSuite = AllureTestReporter(writer = writerWithSuite)

   override val extensions: List<Extension> = listOf(reporterNoSuite, reporterWithSuite)

   init {

      test("suite label equals spec FQN when JVM_SUITE_NAME is not set") {
         val id = writerNoSuite.id(this.testCase).toString()
         writerNoSuite.allure.updateTestCase(id) { result ->
            result.labels.forOne {
               it.name shouldBe ResultsUtils.SUITE_LABEL_NAME
               it.value shouldBe AllureSuiteLabelTest::class.java.canonicalName
            }
            // no sub-suite label should be present
            result.labels.none { it.name == ResultsUtils.SUB_SUITE_LABEL_NAME } shouldBe true
         }
      }

      test("suite label equals JVM_SUITE_NAME and sub-suite label equals spec FQN when JVM_SUITE_NAME is set") {
         val id = writerWithSuite.id(this.testCase).toString()
         writerWithSuite.allure.updateTestCase(id) { result ->
            result.labels.forOne {
               it.name shouldBe ResultsUtils.SUITE_LABEL_NAME
               it.value shouldBe "integrationTest"
            }
            result.labels.forOne {
               it.name shouldBe ResultsUtils.SUB_SUITE_LABEL_NAME
               it.value shouldBe AllureSuiteLabelTest::class.java.canonicalName
            }
         }
      }
   }
}
