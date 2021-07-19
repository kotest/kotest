package io.kotest.core.test

import io.kotest.core.config.configuration
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
   val testCase = TestCase(
      description = spec::class.toDescription().append(name, type),
      spec = spec,
      test = test,
      source = sourceRef(),
      type = type,
      config = config,
      factoryId = null,
      parent = null, // root tests do not have a parent test case
   )
   return if (configuration.testNameAppendTags) {
      TestCase.appendTagsInDisplayName(testCase)
   } else {
      testCase
   }
}
