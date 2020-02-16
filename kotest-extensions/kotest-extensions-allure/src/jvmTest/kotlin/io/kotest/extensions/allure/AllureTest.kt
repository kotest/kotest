package io.kotest.extensions.allure

import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import io.qameta.allure.*
import io.qameta.allure.util.ResultsUtils

@Story("foo-story")
@Owner("foo-owner")
@Epic("foo-epic")
@Feature("foo-feature")
@Severity(SeverityLevel.BLOCKER)
class AllureTestListenerTest : WordSpec() {

   override fun listeners() = listOf(AllureTestListener)

   init {

      "allure test listener" should {
         "detect label annotations" {
            val desc = this.context.testCase.description
            AllureTestListener.allure().updateTestCase(AllureTestListener.uuids[desc].toString()) { result ->
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
