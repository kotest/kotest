package io.kotlintest

import org.junit.runner.Description

data class TestSuite(val name: String) {

  val testCases: MutableList<TestCase> = mutableListOf()
  private val nestedSuites: MutableList<TestSuite> = mutableListOf()

  fun addNestedSuite(nested: TestSuite) {
    nestedSuites.add(nested)
  }

  fun testCasesIncludingChildren(suite: TestSuite = this): List<TestCase> =
      suite.testCases + suite.nestedSuites.flatMap { suite -> testCasesIncludingChildren(suite) }

  operator fun get(index: Int) = testCasesIncludingChildren()[index]

  val description: Description = description(this)

  private fun description(suite: TestSuite = this): Description {
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