package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.ProjectExtension

object ProjectConfig : AbstractProjectConfig() {

  var beforeAll = 0
  var afterAll = 0

  val intercepterLog = StringBuilder()

  override val extensions = listOf(TestExtension)

  override fun beforeAll() {
    intercepterLog.append("B1.")
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