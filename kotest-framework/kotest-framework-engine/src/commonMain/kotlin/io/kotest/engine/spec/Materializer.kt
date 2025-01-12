package io.kotest.engine.spec

import io.kotest.common.KotestInternal
import io.kotest.core.descriptors.append
import io.kotest.core.factory.TestFactory
import io.kotest.core.names.TestName
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.names.DuplicateTestNameHandler
import io.kotest.engine.test.names.TestNameEscaper

/**
 * Materializes [TestCase] at runtime from [RootTest] and [NestedTest] definitions.
 */
@KotestInternal
class Materializer(
   private val specConfigResolver: SpecConfigResolver,
) {

   constructor() : this(SpecConfigResolver())

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
   fun materialize(spec: Spec): List<TestCase> {

      val duplicateTestNameMode = specConfigResolver.duplicateTestNameMode(spec)
      val handler = DuplicateTestNameHandler(duplicateTestNameMode)

      val tests = spec.rootTests().map { rootTest ->

         val uniqueName = handler.handle(rootTest.name)
         val uniqueTestName = if (uniqueName == null) rootTest.name else rootTest.name.copy(name = uniqueName)

         // Note: intellij has a bug, where if a child test has a name that starts with the parent test name,
         // then it will remove the common prefix from the child, to workaround this, we will add a dash at the
         // start of the nested test to make the child nest have a different prefix.
         // Also note: This only affects non-MPP tests, as MPP tests have the platform name added
         val resolvedName = resolvedName(uniqueTestName, null)

         val config = if (rootTest.disabled == true)
            (rootTest.config ?: TestConfig()).withXDisabled()
         else rootTest.config

         TestCase(
            descriptor = spec::class.toDescriptor().append(resolvedName),
            name = resolvedName,
            spec = spec,
            type = rootTest.type,
            source = rootTest.source,
            test = rootTest.test,
            config = config,
            factoryId = rootTest.factoryId,
         )
      }

      return when (specConfigResolver.testCaseOrder(spec)) {
         TestCaseOrder.Sequential -> tests
         TestCaseOrder.Random -> tests.shuffled()
         TestCaseOrder.Lexicographic -> tests.sortedBy { it.name.name }
      }
   }

   /**
    * Materializes a [NestedTest] into a runtime [TestCase] by attaching it to the given parent.
    */
   fun materialize(nested: NestedTest, parent: TestCase): TestCase {

      // Note: intellij has a bug, where if a child test has a name that starts with the parent test name,
      // then it will remove the common prefix from the child, to workaround this, we will add a dash at the
      // start of the nested test to make the child nest have a different prefix.
      // Also note: This only affects non-MPP tests, as MPP tests have the platform name added
      val resolvedName = resolvedName(nested.name, parent.name)

      val config = if (nested.disabled == true)
         (nested.config ?: TestConfig()).withXDisabled()
      else nested.config

      return TestCase(
         descriptor = parent.descriptor.append(resolvedName),
         name = resolvedName,
         spec = parent.spec,
         test = nested.test,
         source = nested.source,
         type = nested.type,
         config = config,
         factoryId = parent.factoryId,
         parent = parent,
      )
   }

   private fun resolvedName(name: TestName, parent: TestName?): TestName {
      val resolvedName = when {
         parent == null -> name.name
         name.name.startsWith(parent.name) -> "- " + name.name
         else -> name.name
      }
      return name.copy(name = TestNameEscaper.escape(resolvedName))
   }
}
