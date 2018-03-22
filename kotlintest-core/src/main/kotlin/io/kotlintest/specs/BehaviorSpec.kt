package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

@Suppress("FunctionName")
abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean {
    if (oneInstancePerTest)
      throw RuntimeException("This spec no longer supports using oneInstancePerTest. Only specs which do not use nested test scopes can use this feature")
    return false
  }

  fun Given(desc: String, init: GivenScope.() -> Unit) = given(desc, init)
  fun given(desc: String, init: GivenScope.() -> Unit) {
    val scope = TestScope("Given $desc", this@BehaviorSpec, { GivenScope(TestScope.empty()).init() })
    rootScope.addScope(scope)
    GivenScope(scope).init()
  }

  inner class GivenScope(private val parent: TestScope) {

    fun and(desc: String, init: GivenScope.() -> Unit) {
      val scope = TestScope("And $desc", this@BehaviorSpec, { GivenScope(TestScope.empty()).init() })
      parent.addScope(scope)
      GivenScope(scope).init()
    }

    fun When(desc: String, init: WhenScope.() -> Unit) = `when`(desc, init)
    fun `when`(desc: String, init: WhenScope.() -> Unit) {
      val scope = TestScope("When $desc", this@BehaviorSpec, { WhenScope(TestScope.empty()).init() })
      parent.addScope(scope)
      WhenScope(scope).init()
    }
  }

  inner class WhenScope(private val parent: TestScope) {

    fun and(desc: String, init: WhenScope.() -> Unit) {
      val scope = TestScope("And $desc", this@BehaviorSpec, { WhenScope(TestScope.empty()).init() })
      parent.addScope(scope)
      WhenScope(scope).init()
    }

    fun Then(desc: String, test: () -> Unit): TestCase = then(desc, test)
    fun then(desc: String, test: () -> Unit): TestCase {
      val tc = TestCase("Then $desc", this@BehaviorSpec, test, defaultTestCaseConfig)
      parent.addTest(tc)
      return tc
    }
  }
}
