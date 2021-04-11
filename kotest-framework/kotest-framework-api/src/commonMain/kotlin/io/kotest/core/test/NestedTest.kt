package io.kotest.core.test

import io.kotest.core.factory.FactoryId
import io.kotest.core.SourceRef
import io.kotest.core.config.configuration
import io.kotest.core.plan.Descriptor
import io.kotest.core.sourceRef
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
   val factoryId: FactoryId?,
   val descriptor: Descriptor.TestDescriptor?,
)

fun createNestedTest(
   name: DescriptionName.TestName,
   xdisabled: Boolean,
   config: TestCaseConfig,
   type: TestType,
   descriptor: Descriptor.TestDescriptor?,
   factoryId: FactoryId?,
   test: suspend TestContext.() -> Unit,
) = NestedTest(
   name = name,
   test = test,
   config = if (xdisabled) config.copy(enabled = false) else config,
   type = type,
   sourceRef = sourceRef(),
   factoryId = factoryId,
   descriptor = descriptor
)

/**
 * Returns a full [TestCase] from this nested test, attaching the nested test to the given spec.
 */
fun NestedTest.toTestCase(spec: Spec, parent: TestCase): TestCase {
   val testCase = TestCase(
      description = parent.description.append(this.name, type),
      spec = spec,
      test = test,
      source = sourceRef,
      type = type,
      config = config,
      factoryId = factoryId,
      assertionMode = null,
      descriptor = descriptor,
      parent = parent,
   )
   return if (configuration.testNameAppendTags) {
      TestCase.appendTagsInDisplayName(testCase)
   } else {
      testCase
   }
}
