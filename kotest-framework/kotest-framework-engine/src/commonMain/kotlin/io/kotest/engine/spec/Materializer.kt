package io.kotest.engine.spec

import io.kotest.core.LogLine
import io.kotest.core.Logger
import io.kotest.core.factory.TestFactory
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.descriptor
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.test.names.DuplicateTestNameHandler

/**
 * Materializes [TestCase]s at runtime from [io.kotest.core.spec.TestDefinition]s.
 */
class Materializer(
   private val specConfigResolver: SpecConfigResolver,
) {

   constructor() : this(SpecConfigResolver())

   private val logger = Logger<Materializer>()

   /**
    * Materializes the root tests from a [Spec] and any [TestFactory]s into
    * [TestCase]s by resolving config at runtime using the supplied project configuration
    * and using spec defaults.
    *
    * Returns the tests using the order specified in the spec, or project configuration if
    * not specified in the spec.
    *
    * Will adjust names to be unique based on the duplicateTestNameMode setting in either
    * the spec or project configuration.
    */
   fun materialize(spec: Spec, ref: SpecRef): List<TestCase> {

      val handler = DuplicateTestNameHandler()
      val mode = specConfigResolver.duplicateTestNameMode(spec)

      val roots = spec.tests()
      logger.log { LogLine(spec::class, "Spec has defined ${roots.size} root tests") }
      val tests = roots.map { rootTest ->

         val unique = handler.unique(mode, rootTest.name)
         val resolvedName = rootTest.name.copy(name = unique)

         val config = if (rootTest.xmethod == TestXMethod.DISABLED)
            (rootTest.config ?: TestConfig()).withXDisabled()
         else rootTest.config

         TestCase(
            descriptor = ref.descriptor().append(resolvedName.name),
            name = resolvedName,
            spec = spec,
            type = rootTest.type,
            source = rootTest.source,
            test = rootTest.test,
            config = config,
            factoryId = rootTest.factoryId,
            xmethod = rootTest.xmethod,
         )
      }

      return sort(tests, spec)
   }

   private fun sort(tests: List<TestCase>, spec: Spec): List<TestCase> {
      val order = specConfigResolver.testCaseOrder(spec)
      logger.log { LogLine(spec::class, "Sorting root tests by $order") }
      return when (order) {
         TestCaseOrder.Sequential -> tests
         TestCaseOrder.Random -> tests.shuffled()
         TestCaseOrder.Lexicographic -> tests.sortedBy { it.name.name }
      }
   }

   /**
    * Materializes a [NestedTest] into a runtime [TestCase] by attaching it to the given parent.
    */
   fun materialize(nested: NestedTest, parent: TestCase): TestCase {

      val config = if (nested.xmethod == TestXMethod.DISABLED)
         (nested.config ?: TestConfig()).withXDisabled()
      else nested.config

      return TestCase(
         descriptor = parent.descriptor.append(nested.name.name),
         name = nested.name,
         spec = parent.spec,
         test = nested.test,
         source = nested.source,
         type = nested.type,
         config = config,
         factoryId = parent.factoryId,
         parent = parent,
         xmethod = nested.xmethod,
      )
   }
}
