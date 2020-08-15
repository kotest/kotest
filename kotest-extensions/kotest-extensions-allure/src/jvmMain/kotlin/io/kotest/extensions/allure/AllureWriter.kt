package io.kotest.extensions.allure

import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.mpp.log
import io.qameta.allure.Allure
import io.qameta.allure.AllureLifecycle
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Issue
import io.qameta.allure.Owner
import io.qameta.allure.Severity
import io.qameta.allure.Story
import io.qameta.allure.model.Label
import io.qameta.allure.model.Status
import io.qameta.allure.model.StatusDetails
import io.qameta.allure.util.ResultsUtils
import java.util.UUID
import kotlin.reflect.full.findAnnotation

class AllureWriter {

   companion object {
      const val LanguageLabel = "kotlin"
      const val FrameworkLabel = "kotest"
   }

   /**
    * Loads the [AllureLifecycle] object which is used to report test lifecycle events.
    */
   val allure = try {
      Allure.getLifecycle() ?: throw IllegalStateException("Allure lifecycle was null")
   } catch (t: Throwable) {
      log("Error getting allure lifecycle", t)
      t.printStackTrace()
      throw t
   }

   private val uuids = mutableMapOf<Description, UUID>()

   fun id(testCase: TestCase) = uuids[testCase.description]

   fun startAllureTestCase(testCase: TestCase) {
      log("Allure beforeTest $testCase")

      val uuid = UUID.randomUUID()
      uuids[testCase.description] = uuid

      val labels = listOfNotNull(
         ResultsUtils.createSuiteLabel(testCase.description.spec().displayName()),
         ResultsUtils.createThreadLabel(),
         ResultsUtils.createHostLabel(),
         ResultsUtils.createLanguageLabel(LanguageLabel),
         ResultsUtils.createFrameworkLabel(FrameworkLabel),
         ResultsUtils.createPackageLabel(testCase.spec::class.java.`package`.name),
         testCase.epic(),
         testCase.story(),
         testCase.feature(),
         testCase.severity(),
         testCase.owner(),
      )

      val links = listOfNotNull(testCase.issue())

      val result = io.qameta.allure.model.TestResult()
         .setFullName(testCase.description.displayPath())
         .setName(testCase.description.name.displayName())
         .setUuid(uuid.toString())
         .setTestCaseId(safeId(testCase.description))
         .setHistoryId(testCase.description.name.displayName())
         .setLabels(labels)
         .setLinks(links)
         .setDescription(testCase.description())

      allure.scheduleTestCase(result)
      allure.startTestCase(uuid.toString())
   }

   fun stopAllureTestCase(testCase: TestCase, result: TestResult) {
      log("Allure afterTest $testCase")
      val uuid = uuids[testCase.description]

      val status = when (result.status) {
         // what we call an error, allure calls a failure
         TestStatus.Error -> Status.BROKEN
         TestStatus.Failure -> Status.FAILED
         TestStatus.Ignored -> Status.SKIPPED
         TestStatus.Success -> Status.PASSED
      }

      val details = StatusDetails()
      details.message = result.error?.message

      allure.updateTestCase(uuid.toString()) {
         it.status = status
         it.statusDetails = details
      }
      allure.stopTestCase(uuid.toString())
      allure.writeTestCase(uuid.toString())
   }

   // returns an id that's acceptable in format for allure
   private fun safeId(description: Description): String = description.id().value
}

fun TestCase.epic(): Label? = this.spec::class.findAnnotation<Epic>()?.let { ResultsUtils.createEpicLabel(it.value) }
fun TestCase.feature(): Label? =
   this.spec::class.findAnnotation<Feature>()?.let { ResultsUtils.createFeatureLabel(it.value) }

fun TestCase.severity(): Label? =
   this.spec::class.findAnnotation<Severity>()?.let { ResultsUtils.createSeverityLabel(it.value) }

fun TestCase.story(): Label? = this.spec::class.findAnnotation<Story>()?.let { ResultsUtils.createStoryLabel(it.value) }
fun TestCase.owner(): Label? = this.spec::class.findAnnotation<Owner>()?.let { ResultsUtils.createOwnerLabel(it.value) }
fun TestCase.issue() = spec::class.findAnnotation<Issue>()?.let { ResultsUtils.createIssueLink(it.value) }
fun TestCase.description() = spec::class.findAnnotation<io.qameta.allure.Description>()?.value
