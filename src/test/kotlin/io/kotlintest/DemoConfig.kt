package io.kotlintest;

object DemoConfig : ProjectConfig() {

  override val extensions = listOf(TestExtension)

  private var started: Long = 0

  override fun beforeAll() {
    started = System.currentTimeMillis()
  }

  override fun afterAll() {
    val time = System.currentTimeMillis() - started
    println("overall time [ms]: " + time) // TODO replace with proper test
  }
}

object TestExtension : ProjectExtension {
  override fun beforeAll() {
    println("before all extension") // TODO replace with proper test
  }

  override fun afterAll() {
    println("after all extension") // TODO replace with proper test
  }
}