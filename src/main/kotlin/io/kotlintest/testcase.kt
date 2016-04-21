package io.kotlintest

data class TestSuite(val name: String, val nestedSuites: MutableList<TestSuite>, val cases: MutableList<io.kotlintest.TestCase>) {
  companion object {
    fun empty(name: String) = TestSuite(name, mutableListOf<TestSuite>(), mutableListOf<io.kotlintest.TestCase>())
  }
}

data class TestCase(val suite: TestSuite, val name: String, val test: () -> Unit)