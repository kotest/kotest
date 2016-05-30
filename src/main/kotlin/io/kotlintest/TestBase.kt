package io.kotlintest

import io.kotlintest.matchers.Matchers
import org.junit.runner.Description
import org.junit.runner.RunWith
import java.util.*

@RunWith(KTestJUnitRunner::class)
abstract class TestBase : Matchers {

  // this should live in some matchers class, but can't inline in an interface :(
  inline fun <reified T> shouldThrow(thunk: () -> Any): T {
    val e = try {
      thunk()
      null
    } catch (e: Throwable) {
      e
    }

    if (e == null)
      throw TestFailedException("Expected exception ${T::class.qualifiedName} but no exception was thrown")
    else if (e.javaClass.name != T::class.qualifiedName)
      throw TestFailedException("Expected exception ${T::class.qualifiedName} but ${e.javaClass.name} was thrown")
    else
      return e as T
  }

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
    val text = if (case.config.invocations < 2) case.name else case.name + " (${case.config.invocations} invocations)"
    return Description.createTestDescription(case.suite.name.replace('.', ' '), text)
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