package io.kotest.engine.spec

import io.kotest.core.config.Configuration
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.config.resolveConfig

/**
 * Materializes [RootTest]s from a [Spec] and any [TestFactory]s into
 * [TestCase]s by resolving config at runtime using the supplied project configuration
 * and using spec defaults.
 *
 * Returns the tests using the order specified in the spec, or project configuration if
 * not specified in the spec.
 */
class Materializer(private val configuration: Configuration) {
   fun materialize(spec: Spec): List<TestCase> {
      val tests = spec.rootTests().map {
         TestCase(
            descriptor = spec::class.toDescriptor().append(it.name),
            name = it.name,
            spec = spec,
            type = it.type,
            source = it.source,
            test = it.test,
            config = resolveConfig(
               config = it.config,
               xdisabled = it.disabled,
               spec = spec,
               configuration = configuration,
            ),
         )
      }
      return when (spec.testCaseOrder() ?: spec.testOrder ?: configuration.testCaseOrder) {
         TestCaseOrder.Sequential -> tests
         TestCaseOrder.Random -> tests.shuffled()
         TestCaseOrder.Lexicographic -> tests.sortedBy { it.name.testName }
      }
   }
}
