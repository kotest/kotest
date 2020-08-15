package io.kotest.core.test

import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription

/**
 * Creates a new root level [TestCase] for the given spec.
 */
fun createRootTestCase(
   spec: Spec,
   name: DescriptionName.TestName,
   test: suspend TestContext.() -> Unit,
   config: TestCaseConfig,
   type: TestType
): TestCase {
   return TestCase(
      spec::class.toDescription().append(name, type),
      spec,
      test,
      sourceRef(),
      type,
      config,
      null,
      null
   )
}
