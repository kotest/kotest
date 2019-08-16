package io.kotlintest.provided

import com.sksamuel.kotlintest.AutoCloseListener
import com.sksamuel.kotlintest.TestCaseFilterTestFilter
import io.kotlintest.AbstractProjectConfig
import io.kotlintest.FailureFirstSpecExecutionOrder
import io.kotlintest.ProjectLevelFilter
import io.kotlintest.SpecExecutionOrder
import io.kotlintest.extensions.ProjectExtension
import io.kotlintest.extensions.TestListener
import io.kotlintest.properties.PropertyTesting

object ProjectConfig : AbstractProjectConfig() {

  var beforeAll = 0
  var afterAll = 0

  val intercepterLog = StringBuilder()

  override fun extensions() = listOf(TestExtension)

  override fun listeners(): List<TestListener> = listOf(AutoCloseListener)

  override fun filters(): List<ProjectLevelFilter> = listOf(TestCaseFilterTestFilter)

  override fun specExecutionOrder(): SpecExecutionOrder = FailureFirstSpecExecutionOrder

  override fun beforeAll() {
    intercepterLog.append("B1.")
  }

  init {
    PropertyTesting.shouldPrintShrinkSteps = false
  }
}

object TestExtension : ProjectExtension {

  override fun beforeAll() {
    ProjectConfig.intercepterLog.append("A1.")
    ProjectConfig.beforeAll++
  }

  override fun afterAll() {
    ProjectConfig.afterAll++
  }
}