package io.kotest.core.test

import io.kotest.core.factory.FactoryId
import io.kotest.core.SourceRef
import io.kotest.core.spec.Spec

/**
 * Describes a test that has been discovered at runtime but has not yet been attached to
 * a parent [TestCase].
 */
data class NestedTest(
   val name: DescriptionName.TestName,
   val test: suspend TestContext.() -> Unit,
   val config: TestCaseConfig,
   val type: TestType,
   val sourceRef: SourceRef,
   val factoryId: FactoryId?
)

/**
 * Returns a full [TestCase] from this nested test, attaching the nested test to the given spec.
 */
fun NestedTest.toTestCase(spec: Spec, parent: Description): TestCase {
   return TestCase(
      description = parent.append(this.name, type),
      spec = spec,
      test = test,
      source = sourceRef,
      type = type,
      config = config,
      factoryId = factoryId,
      assertionMode = null
   )
}
