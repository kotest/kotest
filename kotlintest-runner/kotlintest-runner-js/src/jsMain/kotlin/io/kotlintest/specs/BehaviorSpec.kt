package io.kotlintest.specs

import kotlin.test.Test

@Suppress("FunctionName")
open class BehaviorSpec(body: BehaviorSpec.() -> Unit) {

  init {
    body()
  }

  @Test
  fun kotlintest_generate_tests() {
    tests.forEach { describe(it.name, it.test) }
  }

  private val tests = mutableListOf<TestCase>()

  data class TestCase(val name: String, val suite: Boolean, val test: () -> Unit)

  fun given(name: String, test: GivenBuilder.() -> Unit) {
    describe(name) { GivenBuilder().test() }
  }

  class GivenBuilder {
    fun When(name: String, test: WhenBuilder.() -> Unit) {
      describe(name) { WhenBuilder().test() }
    }
  }

  class WhenBuilder {
    fun then(name: String, test: () -> Unit) {
      it(name, test)
    }
  }
}
