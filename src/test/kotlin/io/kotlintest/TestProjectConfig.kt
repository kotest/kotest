package io.kotlintest

object TestProjectConfig : ProjectConfig() {

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
    TestProjectConfig.intercepterLog.append("A1.")
    TestProjectConfig.beforeAll++
  }

  override fun afterAll() {
    TestProjectConfig.afterAll++
  }
}