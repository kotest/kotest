package io.kotest.engine.test

import io.kotest.core.execution.ExecutionContext
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestNameFormatter

/**
 * Creates a new [TestCase] from this [NestedTest] by attaching the receiver to the given [parent] test.
 *
 * @param overrideName if not null, then the test case name will be set to the given name.
 */
fun NestedTest.attach(parent: TestCase, overrideName: String?, context: ExecutionContext): TestCase {
   val formatter = TestNameFormatter(context.configuration)
   val name = if (overrideName == null) this.name else this.name.copy(testName = overrideName)
   return TestCase(
      parent.descriptor.append(name, formatter.format(name, config.tags + parent.spec.tags()), type),
      spec = parent.spec,
      parent = parent,
      test = test,
      type = type,
      source = source,
      config = config,
      factoryId = factoryId,
   )
}
