package io.kotlintest.specs

import kotlin.test.Test

@Suppress("FunctionName")
open class StringSpec(body: StringSpec.() -> Unit) {

  init {
    body()
  }

  @Test
  fun kotlintest_generate_tests() {
    tests.forEach { describe(it.name, it.test) }
  }

  private val tests = mutableListOf<TestCase>()

  data class TestCase(val name: String, val suite: Boolean, val test: () -> Unit)

  operator fun String.invoke(test: () -> Unit) {
    describe(this, test)
  }
}
