package io.kotlintest;

object DemoConfig : ProjectConfig() {

  override val extensions = listOf(TestExtension)

  val intercepterLog = StringBuilder()

  private var started: Long = 0

  override fun beforeAll() {
    intercepterLog.append("B1.")
    started = System.currentTimeMillis()
  }

  override fun afterAll() {
    val time = System.currentTimeMillis() - started
    println("overall time [ms]: " + time) // TODO replace with proper test
  }
}

object TestExtension : ProjectExtension {
  override fun beforeAll() {
    DemoConfig.intercepterLog.append("A1.")
    println("before all extension") // TODO replace with proper test
  }

  override fun afterAll() {
    println("after all extension") // TODO replace with proper test
  }
}