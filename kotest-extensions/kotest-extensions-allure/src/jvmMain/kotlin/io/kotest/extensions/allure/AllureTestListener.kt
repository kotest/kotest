package io.kotest.extensions.allure

import io.kotest.assertions.log
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.spec.AutoScan
import io.qameta.allure.Allure
import io.qameta.allure.AllureLifecycle
import io.qameta.allure.model.Status
import io.qameta.allure.util.ResultsUtils.*
import java.nio.file.Paths
import java.util.*
import kotlin.reflect.KClass

@AutoScan
object AllureTestListener : TestListener {

   private val uuids = mutableMapOf<Description, UUID>()

   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      Paths.get("allure-results").toFile().deleteRecursively()
   }

   /**
    * Loads the [AllureLifecycle] object which is used to report test lifecycle events.
    */
   private fun allure(): AllureLifecycle = try {
      Allure.getLifecycle() ?: throw IllegalStateException()
   } catch (t: Throwable) {
      log("Error getting allure lifecycle", t)
      t.printStackTrace()
      throw t
   }

   private fun safeId(description: Description): String =
      description.id().replace('/', ' ').replace("[^\\sa-zA-Z0-9]".toRegex(), "")

   override suspend fun beforeTest(testCase: TestCase) {
      log("Allure beforeTest $testCase")

      val uuid = UUID.randomUUID()
      uuids[testCase.description] = uuid

      val labels = listOf(
         createThreadLabel(),
         createHostLabel(),
         createLanguageLabel("kotlin"),
         createFrameworkLabel("kotest")
      )

      val result = io.qameta.allure.model.TestResult()
         .setFullName(testCase.description.fullName())
         .setName(testCase.name)
         .setUuid(uuid.toString())
         .setTestCaseId(safeId(testCase.description))
         .setHistoryId(testCase.description.name)
         .setLabels(labels)

      allure().scheduleTestCase(result)
      allure().startTestCase(uuid.toString())
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      log("Allure afterTest $testCase")
      val uuid = uuids[testCase.description]
      allure().updateTestCase(uuid.toString()) {
         val status = when (result.status) {
            // what we call an error, allure calls a failure
            TestStatus.Error -> Status.BROKEN
            TestStatus.Failure -> Status.FAILED
            TestStatus.Ignored -> Status.SKIPPED
            TestStatus.Success -> Status.PASSED
         }
         it.status = status
      }
      allure().stopTestCase(uuid.toString())
      allure().writeTestCase(uuid.toString())
   }
}
