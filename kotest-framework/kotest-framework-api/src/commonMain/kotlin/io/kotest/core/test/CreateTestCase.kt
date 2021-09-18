package io.kotest.core.test

import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec

/**
 * Creates a new root level [TestCase] for the given spec.
 */
fun createRootTestCase(
   spec: Spec,
   name: TestName,
   test: suspend TestContext.() -> Unit,
   config: TestCaseConfig,
   type: TestType
): TestCase {
   return TestCase(
      descriptor = spec::class.toDescriptor().append(name),
      name = name,
      spec = spec,
      test = test,
      source = sourceRef(),
      type = type,
      config = config,
      factoryId = null,
      parent = null, // root tests do not have a parent test case
   )
}
