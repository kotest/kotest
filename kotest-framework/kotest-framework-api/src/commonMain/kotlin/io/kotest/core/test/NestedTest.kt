package io.kotest.core.test

import io.kotest.core.SourceRef
import io.kotest.core.config.Configuration
import io.kotest.core.descriptors.append
import io.kotest.core.names.TestName
import io.kotest.core.spec.Spec
import io.kotest.core.test.config.UnresolvedTestConfig
import io.kotest.core.test.config.resolveConfig

/**
 * Describes a test that has been discovered at runtime but has not yet been
 * attached to a parent [TestCase].
 */
data class NestedTest(
   val name: TestName,
   val disabled: Boolean,
   val config: UnresolvedTestConfig?, // can be null if the test does not specify config
   val type: TestType,
   val source: SourceRef,
   val test: suspend TestContext.() -> Unit,
)

/**
 * Materializes a runtime [TestCase] from this [NestedTest], attaching the test to the given spec.
 */
fun NestedTest.toTestCase(
   spec: Spec,
   parent: TestCase,
   configuration: Configuration
): TestCase {

   val resolvedTestConfig = resolveConfig(config, disabled, spec, configuration)

   return TestCase(
      descriptor = parent.descriptor.append(name),
      name = name,
      spec = spec,
      test = test,
      source = source,
      type = type,
      config = resolvedTestConfig,
      factoryId = parent.factoryId,
      parent = parent,
   )
}
