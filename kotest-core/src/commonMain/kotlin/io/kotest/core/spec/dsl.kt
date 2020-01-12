package io.kotest.core.spec

import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

interface SpecDsl {

   val defaultTestCaseConfig: TestCaseConfig

   val addTest: (
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) -> Unit
}
