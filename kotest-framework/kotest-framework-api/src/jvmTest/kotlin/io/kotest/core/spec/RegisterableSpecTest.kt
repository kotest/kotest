package io.kotest.core.spec

import io.kotest.core.plan.TestName
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestType
import io.kotest.matchers.collections.shouldExist

class RegisterableSpecTest : FunSpec({
   test("registering tests") {
      val s = object : RegisterableSpec() {
      }
      s.addTest(TestName("foo"), { }, TestCaseConfig(), TestType.Test)
      s.addTest(TestName("bar"), { }, TestCaseConfig(), TestType.Test)
      s.materializeRootTests().shouldExist { it.testCase.name.testName == "foo" }
      s.materializeRootTests().shouldExist { it.testCase.name.testName == "bar" }
   }
})
