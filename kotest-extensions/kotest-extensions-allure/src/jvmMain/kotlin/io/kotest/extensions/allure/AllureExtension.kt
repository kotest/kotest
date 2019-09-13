package io.kotest.extensions.allure

import io.kotest.Description
import io.kotest.Spec
import io.kotest.TestResult
import io.kotest.TestStatus
import io.kotest.extensions.TestListener
import io.qameta.allure.Allure
import io.qameta.allure.model.Label
import io.qameta.allure.model.Status
import org.slf4j.LoggerFactory
import java.nio.file.Paths

object AllureExtension : TestListener {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun beforeSpec(description: Description, spec: Spec) {
    Paths.get("allure-results").toFile().deleteRecursively()
  }

  private val allure = try {
    Allure.getLifecycle() ?: throw IllegalStateException()
  } catch (t: Throwable) {
    logger.error("Error getting allure lifecycle", t)
    t.printStackTrace()
    throw t
  }

  fun safeId(description: Description): String = description.id().replace('/', ' ').replace("[^\\sa-zA-Z0-9]".toRegex(), "")

  override fun beforeTest(description: Description) {
    try {
      allure.scheduleTestCase(io.qameta.allure.model.TestResult()
          .withTestCaseId(safeId(description))
          .withUuid(safeId(description)))
      allure.startTestCase(safeId(description))
    } catch (t: Throwable) {
      logger.error("Error updating allure", t)
      t.printStackTrace()
    }
  }

  override fun afterTest(description: Description, result: TestResult) {
    try {
      allure.updateTestCase(safeId(description)) {
        when (result.status) {
          // what we call an error, allure calls a failure
          TestStatus.Error -> it.status = Status.BROKEN
          TestStatus.Failure -> it.status = Status.FAILED
          TestStatus.Ignored -> it.status = Status.SKIPPED
          TestStatus.Success -> it.status = Status.PASSED
        }
        it.withFullName(description.fullName())
        val severity = result.metaData["Severity"]
        when (severity) {
          is Severity -> {
            it.withLabels(Label().withName("Severity").withValue(severity.level.name))
          }
          else -> {
          }
        }
      }
      allure.stopTestCase(safeId(description))
      allure.writeTestCase(safeId(description))
    } catch (t: Throwable) {
      logger.error("Error updating allure", t)
      t.printStackTrace()
    }
  }
}