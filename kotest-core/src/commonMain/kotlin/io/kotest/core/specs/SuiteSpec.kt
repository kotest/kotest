package io.kotest.core.specs

import io.kotest.core.*
import io.kotest.core.spec.description
import io.kotest.core.test.*

abstract class SuiteSpec(body: SuiteSpec.() -> Unit = {}) : AbstractSpec() {

   init {
      body()
   }

   fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
      rootTestCases.add(
          TestCase(
              this::class.description().append(name),
              FakeSpecConfiguration(),
              { SuiteScope(this).test() },
              sourceRef(),
              TestType.Container,
             TestCaseConfig(),
              null,
              null
          )
      )
   }

   @KotestDsl
   inner class SuiteScope(val context: TestContext) {
      suspend fun test(name: String, test: suspend TestContext.() -> Unit) {
         context.registerTestCase(name, this@SuiteSpec, test,
            TestCaseConfig(), TestType.Test)
      }
   }
}
