package io.kotlintest

import io.kotlintest.matchers.Matchers
import org.junit.runner.Description
import org.junit.runner.RunWith
import java.util.*

@RunWith(KTestJUnitRunner::class)
abstract class TestBase : Matchers {

  open val oneInstancePerTest = false

  // the root test suite which uses the simple name of the class as the name of the suite
  // spec implementations will add their tests to this suite
  internal val root = TestSuite(javaClass.simpleName, ArrayList<TestSuite>(), ArrayList<TestCase>())

  // returns a jUnit Description for the currently registered tests
  internal fun getDescription(): Description {
    return descriptionForSuite(root)
  }

  internal fun descriptionForSuite(suite: TestSuite): Description {
    val desc = Description.createSuiteDescription(suite.name.replace('.', ' '))
    for (nestedSuite in suite.nestedSuites) {
      desc.addChild(descriptionForSuite(nestedSuite))
    }
    for (case in suite.cases) {
      desc.addChild(descriptionForTest(case))
    }
    return desc
  }

  internal fun descriptionForTest(case: TestCase): Description? {
    return Description.createTestDescription(case.suite.name.replace('.', ' '), case.name)
  }

  open fun beforeAll(): Unit {
  }

  open fun afterAll(): Unit {
  }

  open fun beforeEach(): Unit {
  }

  open fun afterEach(): Unit {
  }
}