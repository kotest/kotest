package io.kotlintest

data class TestSuite(
    val name: String,
    val nestedSuites: MutableList<TestSuite>,
    val cases: MutableList<TestCase>) {

  companion object {
    fun empty(name: String) = TestSuite(name, mutableListOf<TestSuite>(), mutableListOf<TestCase>())
  }

  internal fun tests(suite: TestSuite = this): List<TestCase> =
      suite.cases + suite.nestedSuites.flatMap { suite -> tests(suite) }

  internal val size = tests().size
}