package io.kotlintest

import org.junit.runner.Description

data class TestSuite(val name: String) {

  internal val testCases: MutableList<TestCase> = mutableListOf()
  private val nestedSuites: MutableList<TestSuite> = mutableListOf()

  fun addTestCase(testCase: TestCase) {
    testCases.add(testCase)
  }

  fun addNestedSuite(nested: TestSuite) {
    nestedSuites.add(nested)
  }

  fun testCasesIncludingChildren(suite: TestSuite = this): List<TestCase> =
      suite.testCases + suite.nestedSuites.flatMap { suite -> testCasesIncludingChildren(suite) }

  operator fun get(index: Int) = testCasesIncludingChildren()[index]

  fun description(): Description = description(this)

  private fun description(suite: TestSuite): Description {
    val desc = Description.createSuiteDescription(suite.name.replace('.', ' '))
    for (nestedSuite in suite.nestedSuites) {
      desc.addChild(description(nestedSuite))
    }
    for (case in suite.testCases) {
      desc.addChild(case.description)
    }
    return desc
  }
}