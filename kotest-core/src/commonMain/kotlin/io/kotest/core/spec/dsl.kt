package io.kotest.core.spec

import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.core.TestType

interface SpecDsl {

   val defaultTestCaseConfig: TestCaseConfig

   val addTest: (
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) -> Unit
}
