package io.kotest.core.test

import io.kotest.core.SourceRef
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.append
import io.kotest.core.factory.FactoryId
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec

/**
 * Describes a test that has been discovered at runtime but has not yet been attached to
 * a parent [TestCase].
 */
data class NestedTest(
   val descriptor: Descriptor.TestDescriptor,
   val name: TestName,
   val test: suspend TestContext.() -> Unit,
   val config: TestCaseConfig,
   val type: TestType,
   val sourceRef: SourceRef,
   val factoryId: FactoryId?,
)

fun createNestedTest(
   name: TestName,
   descriptor: Descriptor.TestDescriptor,
   xdisabled: Boolean,
   config: TestCaseConfig,
   type: TestType,
   factoryId: FactoryId?,
   test: suspend TestContext.() -> Unit,
) = NestedTest(
   descriptor = descriptor,
   name = name,
   test = test,
   config = if (xdisabled) config.copy(enabled = false) else config,
   type = type,
   sourceRef = sourceRef(),
   factoryId = factoryId,
)

/**
 * Realizes a runtime [TestCase] from this [NestedTest], attaching the test to the given spec.
 */
fun NestedTest.toTestCase(spec: Spec, parent: TestCase): TestCase {
   return TestCase(
      descriptor = parent.descriptor.append(name),
      name = name,
      spec = spec,
      test = test,
      source = sourceRef,
      type = type,
      config = config,
      factoryId = factoryId,
      parent = parent,
   )
}
