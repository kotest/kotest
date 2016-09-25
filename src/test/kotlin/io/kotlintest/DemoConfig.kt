package io.kotlintest;

object DemoConfig : ProjectConfig() {
  override val extensions = listOf(TestExtension)
}

object TestExtension : ProjectExtension {
  override fun beforeAll() {
    println("before all")
  }

  override fun afterAll() {
    println("after all")
  }
}