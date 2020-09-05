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
import io.qameta.allure.model.StepResult
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

   private val uuids = mutableMapOf<Description, String>()

   fun id(testCase: TestCase) = uuids[testCase.description]

   fun startTestCase(testCase: TestCase) {
      log("Allure beforeTest $testCase")

      val labels = listOfNotNull(
         testCase.epic(),
         testCase.feature(),
         ResultsUtils.createFrameworkLabel(FrameworkLabel),
         ResultsUtils.createHostLabel(),
         ResultsUtils.createLanguageLabel(LanguageLabel),
         testCase.owner(),
         ResultsUtils.createPackageLabel(testCase.spec::class.java.`package`.name),
         ResultsUtils.createSuiteLabel(testCase.description.spec().displayName()),
         testCase.severity(),
         testCase.story(),
         ResultsUtils.createThreadLabel(),
      )

      val links = listOfNotNull(testCase.issue())
      val uuid = UUID.randomUUID().toString()
      uuids[testCase.description] = uuid

      val result = io.qameta.allure.model.TestResult()
         .setFullName(testCase.description.testDisplayPath().value)
         .setName(testCase.description.testDisplayPath().value)
         .setUuid(uuid)
         .setTestCaseId(safeId(testCase.description))
         .setHistoryId(testCase.description.name.displayName)
         .setLabels(labels)
         .setLinks(links)
         .setDescription(testCase.description())

      allure.scheduleTestCase(result)
      allure.startTestCase(uuid)
   }

   fun finishTestCase(testCase: TestCase, result: TestResult) {
      log("Allure afterTest $testCase")
      val status = when (result.status) {
         // what we call an error, allure calls broken
         TestStatus.Error -> Status.BROKEN
         TestStatus.Failure -> Status.FAILED
         TestStatus.Ignored -> Status.SKIPPED
         TestStatus.Success -> Status.PASSED
      }

      val uuid = uuids[testCase.description]
      val details = ResultsUtils.getStatusDetails(result.error)

      allure.updateTestCase(uuid) {
         it.status = status
         it.statusDetails = details.orElseGet { null }
         testCase.description.parents().forEach { d ->
            it.steps.add(StepResult()
               .setName(d.displayName())
               .setStatus(Status.PASSED)
               .setStart(0L)
               .setStop(0L)
            )
         }
      }
      allure.stopTestCase(uuid)
      allure.writeTestCase(uuid)
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
