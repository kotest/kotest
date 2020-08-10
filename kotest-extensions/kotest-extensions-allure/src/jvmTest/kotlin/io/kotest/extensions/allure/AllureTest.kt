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

   private val allure = AllureTestReporter()

   override fun listeners() = listOf(allure)

   init {

      "allure test listener" should {
         "detect label annotations" {
            val id = allure.writer.id(this.context.testCase).toString()
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
