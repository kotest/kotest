package io.kotlintest.extensions.allure

import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.extensions.TestListener
import io.qameta.allure.Allure
import io.qameta.allure.model.Label
import io.qameta.allure.model.Status

object AllureExtension : TestListener {

  private val allure = Allure.getLifecycle() ?: throw IllegalStateException("Allure lifecycle not found")

  private fun safeId(description: Description): String = description.id().replace('/', ' ').replace("[^\\sa-zA-Z0-9]".toRegex(), "")

  override fun testStarted(description: Description) {
    allure.scheduleTestCase(io.qameta.allure.model.TestResult()
        .withTestCaseId(safeId(description))
        .withUuid(safeId(description)))
    allure.startTestCase(safeId(description))
  }

  override fun testFinished(description: Description, result: TestResult) {
    allure.updateTestCase(safeId(description), {
      when (result.status) {
        TestStatus.Failed -> it.withStatus(Status.FAILED)
        TestStatus.Ignored -> it.withStatus(Status.SKIPPED)
        TestStatus.Passed -> it.withStatus(Status.PASSED)
      }
      it.withFullName(description.fullName())
      result.metaData.filterIsInstance<Severity>().map { it.level.name }.forEach { value ->
        it.withLabels(Label().withName("Severity").withValue(value))
      }
    })
    allure.stopTestCase(safeId(description))
    try {
      allure.writeTestCase(safeId(description))
    } catch (t: Throwable) {

    }
  }
}