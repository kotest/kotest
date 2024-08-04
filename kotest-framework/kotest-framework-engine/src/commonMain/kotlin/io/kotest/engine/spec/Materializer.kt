package io.kotest.engine.spec

import io.kotest.common.KotestInternal
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.engine.test.TestConfigResolver
import io.kotest.engine.test.names.DuplicateTestNameHandler

/**
 * Materializes [TestCase] at runtime from [RootTest] and [NestedTest] definitions.
 */
@KotestInternal
class Materializer(private val configuration: ProjectConfiguration) {

   /**
    * Materializes [RootTest]s from a [Spec] and any [TestFactory]s into
    * [TestCase]s by resolving config at runtime using the supplied project configuration
    * and using spec defaults.
    *
    * Returns the tests using the order specified in the spec, or project configuration if
    * not specified in the spec.
    *
    * Will adjust names to be unique based on the duplicateTestNameMode setting in either
    * the spec or project configuration.
    */
   fun materialize(spec: Spec): List<TestCase> {

      val duplicateTestNameMode = spec.duplicateTestNameMode ?: configuration.duplicateTestNameMode
      val handler = DuplicateTestNameHandler(duplicateTestNameMode)

      val tests = spec.rootTests().map { rootTest ->

         val uniqueName = handler.handle(rootTest.name)
         val uniqueTestName = if (uniqueName == null) rootTest.name else rootTest.name.copy(testName = uniqueName)

         TestCase(
            descriptor = spec::class.toDescriptor().append(uniqueTestName),
            name = uniqueTestName,
            spec = spec,
            type = rootTest.type,
            source = rootTest.source,
            test = rootTest.test,
            config = TestConfigResolver(configuration).resolve(
               testConfig = rootTest.config,
               xdisabled = rootTest.disabled,
               parent = null,
               spec = spec,
            ),
            factoryId = rootTest.factoryId,
         )
      }

      return when (spec.testCaseOrder() ?: spec.testOrder ?: configuration.testCaseOrder) {
         TestCaseOrder.Sequential -> tests
         TestCaseOrder.Random -> tests.shuffled()
         TestCaseOrder.Lexicographic -> tests.sortedBy { it.name.testName }
      }
   }

   /**
    * Materializes a [NestedTest] into a runtime [TestCase] by attaching it to the given parent.
    */
   fun materialize(nested: NestedTest, parent: TestCase): TestCase {
      return TestCase(
         descriptor = parent.descriptor.append(nested.name),
         name = nested.name,
         spec = parent.spec,
         test = nested.test,
         source = nested.source,
         type = nested.type,
         config = TestConfigResolver(configuration).resolve(
            testConfig = nested.config,
            xdisabled = nested.disabled,
            parent = parent,
            spec = parent.spec,
         ),
         factoryId = parent.factoryId,
         parent = parent,
      )

   }
}
