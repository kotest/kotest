package io.kotest.provided

import com.sksamuel.kotest.AutoCloseListener
import com.sksamuel.kotest.TestCaseFilterTestFilter
import io.kotest.AbstractProjectConfig
import io.kotest.FailureFirstSpecExecutionOrder
import io.kotest.SpecExecutionOrder
import io.kotest.extensions.ProjectLevelFilter
import io.kotest.extensions.ProjectListener
import io.kotest.extensions.TestListener
import io.kotest.properties.PropertyTesting

object ProjectConfig : AbstractProjectConfig() {

  var beforeAll = 0
  var afterAll = 0

  val intercepterLog = StringBuilder()

  override fun listeners(): List<TestListener> = listOf(AutoCloseListener)

  override fun projectListeners() = listOf(TestProjectListener)

  override fun filters(): List<ProjectLevelFilter> = listOf(TestCaseFilterTestFilter)

  override fun specExecutionOrder(): SpecExecutionOrder = FailureFirstSpecExecutionOrder

  override fun beforeAll() {
    intercepterLog.append("B1.")
  }

  init {
    PropertyTesting.shouldPrintShrinkSteps = false
  }
}

object TestProjectListener : ProjectListener {

  override fun beforeProject() {
    ProjectConfig.intercepterLog.append("A1.")
    ProjectConfig.beforeAll++
  }

  override fun afterProject() {
    ProjectConfig.afterAll++
  }
}
