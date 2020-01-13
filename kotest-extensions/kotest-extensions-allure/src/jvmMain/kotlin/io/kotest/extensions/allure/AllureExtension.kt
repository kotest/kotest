package io.kotest.extensions.allure

import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.extensions.TestListener
import io.qameta.allure.Allure
import io.qameta.allure.model.Label
import io.qameta.allure.model.Status
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import kotlin.reflect.KClass

object AllureExtension : TestListener {

   private val logger = LoggerFactory.getLogger(javaClass)

   override fun prepareSpec(kclass: KClass<out SpecConfiguration>) {
      Paths.get("allure-results").toFile().deleteRecursively()
   }

   private val allure = try {
      Allure.getLifecycle() ?: throw IllegalStateException()
   } catch (t: Throwable) {
      logger.error("Error getting allure lifecycle", t)
      t.printStackTrace()
      throw t
   }

   private fun safeId(description: Description): String =
      description.id().replace('/', ' ').replace("[^\\sa-zA-Z0-9]".toRegex(), "")

   override suspend fun beforeTest(testCase: TestCase) {
      try {
         allure.scheduleTestCase(
            io.qameta.allure.model.TestResult()
               .setTestCaseId(safeId(testCase.description))
               .setUuid(safeId(testCase.description))
         )
         allure.startTestCase(safeId(testCase.description))
      } catch (t: Throwable) {
         logger.error("Error updating allure", t)
         t.printStackTrace()
      }
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      try {
         allure.updateTestCase(safeId(testCase.description)) {
            when (result.status) {
               // what we call an error, allure calls a failure
               TestStatus.Error -> it.status = Status.BROKEN
               TestStatus.Failure -> it.status = Status.FAILED
               TestStatus.Ignored -> it.status = Status.SKIPPED
               TestStatus.Success -> it.status = Status.PASSED
            }
            it.fullName = testCase.description.fullName()
            when (val severity = result.metaData["Severity"]) {
               is Severity -> {
                  it.labels.add(Label().setName("Severity").setValue(severity.level.name))
               }
               else -> {
               }
            }
         }
         allure.stopTestCase(safeId(testCase.description))
         allure.writeTestCase(safeId(testCase.description))
      } catch (t: Throwable) {
         logger.error("Error updating allure", t)
         t.printStackTrace()
      }
   }
}
