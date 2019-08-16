package io.kotlintest.specs

import kotlin.test.Test

@Suppress("FunctionName")
open class FunSpec(body: FunSpec.() -> Unit) {

  init {
    body()
  }

  @Test
  fun kotlintest_generate_tests() {
    tests.forEach { describe(it.name, it.test) }
  }

  private val tests = mutableListOf<TestCase>()

  data class TestCase(val name: String, val suite: Boolean, val test: () -> Unit)

  fun test(name: String, test: () -> Unit) {
    it(name, test)
  }
}
