package io.kotlintest;

object DemoConfig : ProjectConfig() {

  override val extensions = listOf(TestExtension)

  val intercepterLog = StringBuilder()

  private var started: Long = 0

  override fun beforeAll() {
    intercepterLog.append("B1.")
    started = System.currentTimeMillis()
  }
}

object TestExtension : ProjectExtension {
  override fun beforeAll() {
    DemoConfig.intercepterLog.append("A1.")
  }
}