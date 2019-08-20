package io.kotlintest.provided

import com.sksamuel.kotlintest.AutoCloseListener
import com.sksamuel.kotlintest.TestCaseFilterTestFilter
import io.kotlintest.AbstractProjectConfig
import io.kotlintest.FailureFirstSpecExecutionOrder
import io.kotlintest.SpecExecutionOrder
import io.kotlintest.extensions.ProjectLevelFilter
import io.kotlintest.extensions.ProjectListener
import io.kotlintest.extensions.TestListener
import io.kotlintest.properties.PropertyTesting

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
