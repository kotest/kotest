package io.kotest.engine.spec

import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.engine.test.toDescription

fun Spec.createTestCase(
   name: DescriptionName.TestName,
   test: suspend TestContext.() -> Unit,
   config: TestCaseConfig,
   type: TestType
): TestCase {
   return TestCase(
       this::class.toDescription().append(name, type),
       this,
       test,
       sourceRef(),
       type,
       config,
       null,
       null
   )
}
