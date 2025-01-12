package io.kotest.extensions.allure

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Owner
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Story
import io.qameta.allure.util.ResultsUtils

@Story("foo-story")
@Owner("foo-owner")
@Epic("foo-epic")
@Feature("foo-feature")
@Severity(SeverityLevel.CRITICAL)
class AllureTestListenerTest : WordSpec() {

   private val allure = AllureTestReporter()

   override val extensions: List<Extension> = listOf(allure)

   init {
      "allure test listener" should {
         "detect label annotations" {
            val id = allure.writer.id(this.testCase).toString()
            allure.writer.allure.updateTestCase(id) { result ->
               result.labels.forOne {
                  it.name shouldBe ResultsUtils.OWNER_LABEL_NAME
                  it.value shouldBe "foo-owner"
               }
               result.labels.forOne {
                  it.name shouldBe ResultsUtils.STORY_LABEL_NAME
                  it.value shouldBe "foo-story"
               }
               result.labels.forOne {
                  it.name shouldBe ResultsUtils.EPIC_LABEL_NAME
                  it.value shouldBe "foo-epic"
               }
               result.labels.forOne {
                  it.name shouldBe ResultsUtils.SEVERITY_LABEL_NAME
                  it.value shouldBe "critical"
               }
               result.labels.forOne {
                  it.name shouldBe ResultsUtils.FEATURE_LABEL_NAME
                  it.value shouldBe "foo-feature"
               }
            }
         }
      }

      "allure test listener w severity for testCase" should {
         "detect label annotations and override severity for this testCase".config(severity = TestCaseSeverityLevel.BLOCKER) {
            val id = allure.writer.id(this.testCase).toString()
            allure.writer.allure.updateTestCase(id) { result ->
               result.labels.forOne {
                  it.name shouldBe ResultsUtils.OWNER_LABEL_NAME
                  it.value shouldBe "foo-owner"
               }
               result.labels.forOne {
                  it.name shouldBe ResultsUtils.STORY_LABEL_NAME
                  it.value shouldBe "foo-story"
               }
               result.labels.forOne {
                  it.name shouldBe ResultsUtils.EPIC_LABEL_NAME
                  it.value shouldBe "foo-epic"
               }
               result.labels.forOne {
                  it.name shouldBe ResultsUtils.SEVERITY_LABEL_NAME
                  it.value shouldBe "blocker"
               }
               result.labels.forOne {
                  it.name shouldBe ResultsUtils.FEATURE_LABEL_NAME
                  it.value shouldBe "foo-feature"
               }
            }
         }
      }
   }
}
