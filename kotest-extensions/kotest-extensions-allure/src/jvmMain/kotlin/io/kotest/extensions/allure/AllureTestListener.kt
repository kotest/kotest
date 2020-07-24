package io.kotest.extensions.allure

import io.kotest.mpp.log
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.test.*
import io.kotest.core.test.Description
import io.qameta.allure.*
import io.qameta.allure.model.Label
import io.qameta.allure.model.Status
import io.qameta.allure.model.StatusDetails
import io.qameta.allure.util.ResultsUtils.*
import java.nio.file.Paths
import java.util.*
import kotlin.reflect.full.findAnnotation

fun TestCase.epic(): Label? = this.spec::class.findAnnotation<Epic>()?.let { createEpicLabel(it.value) }
fun TestCase.feature(): Label? = this.spec::class.findAnnotation<Feature>()?.let { createFeatureLabel(it.value) }
fun TestCase.severity(): Label? = this.spec::class.findAnnotation<Severity>()?.let { createSeverityLabel(it.value) }
fun TestCase.story(): Label? = this.spec::class.findAnnotation<Story>()?.let { createStoryLabel(it.value) }
fun TestCase.owner(): Label? = this.spec::class.findAnnotation<Owner>()?.let { createOwnerLabel(it.value) }
fun TestCase.issue() = spec::class.findAnnotation<Issue>()?.let { createIssueLink(it.value) }
fun TestCase.description() = spec::class.findAnnotation<io.qameta.allure.Description>()?.value

@AutoScan
object AllureTestListener : TestListener, ProjectListener {

   override val name = "AllureTestListener"

   internal val uuids = mutableMapOf<Description, UUID>()

   /**
    * Loads the [AllureLifecycle] object which is used to report test lifecycle events.
    */
   internal fun allure(): AllureLifecycle = try {
      Allure.getLifecycle() ?: throw IllegalStateException()
   } catch (t: Throwable) {
      log("Error getting allure lifecycle", t)
      t.printStackTrace()
      throw t
   }

   override suspend fun beforeProject() {
      Paths.get("./allure-results").toFile().deleteRecursively()
   }

   private fun safeId(description: Description): String =
      description.id().replace('/', ' ').replace("[^\\sa-zA-Z0-9]".toRegex(), "")

   override suspend fun beforeAny(testCase: TestCase) {
      startAllureTestCase(testCase)
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      stopAllureTestCase(testCase, result)
   }

   private fun startAllureTestCase(testCase: TestCase) {
      log("Allure beforeTest $testCase")

      val uuid = UUID.randomUUID()
      uuids[testCase.description] = uuid

      val labels = listOfNotNull(
         createSuiteLabel(testCase.description.spec().name.displayName()),
         createThreadLabel(),
         createHostLabel(),
         createLanguageLabel("kotlin"),
         createFrameworkLabel("kotest"),
         createPackageLabel(testCase.spec::class.java.`package`.name),
         testCase.epic(),
         testCase.story(),
         testCase.feature(),
         testCase.severity(),
         testCase.owner()
      )

      val links = listOfNotNull(
         testCase.issue()
      )

      val result = io.qameta.allure.model.TestResult()
         .setFullName(testCase.description.fullName())
         .setName(testCase.description.name.displayName())
         .setUuid(uuid.toString())
         .setTestCaseId(safeId(testCase.description))
         .setHistoryId(testCase.description.name.displayName())
         .setLabels(labels)
         .setLinks(links)
         .setDescription(testCase.description())

      allure().scheduleTestCase(result)
      allure().startTestCase(uuid.toString())
   }

   private fun stopAllureTestCase(testCase: TestCase, result: TestResult) {
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

      allure().updateTestCase(uuid.toString()) {
         it.status = status
         it.statusDetails = details
      }
      allure().stopTestCase(uuid.toString())
      allure().writeTestCase(uuid.toString())
   }
}
