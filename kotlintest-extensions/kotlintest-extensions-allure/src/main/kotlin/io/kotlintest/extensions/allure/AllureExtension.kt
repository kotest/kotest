package io.kotlintest.extensions.allure

import io.kotlintest.Description
import io.kotlintest.extensions.TestListener
import io.kotlintest.extensions.TestResult
import io.kotlintest.extensions.TestStatus
import io.qameta.allure.Allure
import io.qameta.allure.model.Status

object AllureExtension : TestListener {

  private val allure = Allure.getLifecycle() ?: throw IllegalStateException("Allure lifecycle not found")

  override fun testStarted(description: Description) {
    allure.scheduleTestCase(io.qameta.allure.model.TestResult()
        .withTestCaseId(description.id())
        .withUuid(description.id()))
    println("a" + description)
    allure.startTestCase(description.id())
  }

  override fun testFinished(description: Description, result: TestResult) {
    println("b" + description)
    allure.updateTestCase(description.id(), {
      when (result.status) {
        TestStatus.Failed -> it.withStatus(Status.FAILED)
        TestStatus.Ignored -> it.withStatus(Status.SKIPPED)
        TestStatus.Passed -> it.withStatus(Status.PASSED)
      }
    })
    allure.stopTestCase(description.id())
    allure.writeTestCase(description.id())
  }
}