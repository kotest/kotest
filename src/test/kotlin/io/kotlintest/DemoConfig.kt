package io.kotlintest;

object DemoConfig : ProjectConfig() {

  override val extensions = listOf(TestExtension)

  val intercepterLog = StringBuilder()

  override fun beforeAll() {
    intercepterLog.append("B1.")
  }
}

object TestExtension : ProjectExtension {
  override fun beforeAll() {
    DemoConfig.intercepterLog.append("A1.")
  }
}