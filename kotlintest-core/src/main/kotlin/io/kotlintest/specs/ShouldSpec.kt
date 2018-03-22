package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

/**
 * Example:
 *
 * "some test" {
 *   "with context" {
 *      should("do something") {
 *        // test here
 *      }
 *    }
 *  }
 *
 *  or
 *
 *  should("do something") {
 *    // test here
 *  }
 */
abstract class ShouldSpec(body: ShouldSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean {
    if (oneInstancePerTest)
      throw RuntimeException("This spec no longer supports using oneInstancePerTest. Only specs which do not use nested test scopes can use this feature")
    return false
  }

  operator fun String.invoke(init: ShouldScope.() -> Unit) {
    val scope = TestScope(this, this@ShouldSpec, { ShouldScope(TestScope.empty()).init() })
    rootScope.addScope(scope)
    ShouldScope(scope).init()
  }

  inner class ShouldScope(private val parentScope: TestScope) {

    operator fun String.invoke(init: ShouldScope.() -> Unit) {
      val scope = TestScope(this, this@ShouldSpec, { ShouldScope(TestScope.empty()).init() })
      parentScope.addScope(scope)
      ShouldScope(scope).init()
    }

    fun should(name: String, test: () -> Unit): TestCase = addTest(name, test, parentScope)
  }

  private fun addTest(name: String, test: () -> Unit, parentDescriptor: TestScope): TestCase {
    val tc = TestCase("should $name", this@ShouldSpec, test, defaultTestCaseConfig)
    parentDescriptor.addTest(tc)
    return tc
  }

  fun should(name: String, test: () -> Unit): TestCase = addTest(name, test, rootScope)
}