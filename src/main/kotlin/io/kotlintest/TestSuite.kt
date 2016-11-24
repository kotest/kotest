package io.kotlintest

data class TestSuite(val name: String) {

  val nestedSuites: MutableList<TestSuite> = mutableListOf()
  val testCases: MutableList<TestCase> = mutableListOf()

  fun tests(suite: TestSuite = this): List<TestCase> =
      suite.testCases + suite.nestedSuites.flatMap { suite -> tests(suite) }

  internal val size = tests().size
}