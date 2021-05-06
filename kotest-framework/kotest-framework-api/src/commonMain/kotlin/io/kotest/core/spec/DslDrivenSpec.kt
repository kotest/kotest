package io.kotest.core.spec

import io.kotest.core.SourceRef
import io.kotest.core.Tuple2
import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.addPrefix
import io.kotest.core.internal.isEnabled
import io.kotest.core.internal.tags.tags
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.plan.NodeName
import io.kotest.core.plan.Source
import io.kotest.core.plan.append
import io.kotest.core.plan.toNode
import io.kotest.core.source
import io.kotest.core.sourceRef
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import kotlin.reflect.KClass

/**
 * Base class for specs that allow for registration of tests via the DSL.
 */
abstract class DslDrivenSpec : Spec() {

   /**
    * Contains the root [RootTestCase]s defined in this spec.
    */
   private var rootTestCases = emptyList<RootTestCase>()

   override suspend fun materializeRootTests(): List<RootTest> {
      return rootTestCases.withIndex().map {
         val tc = it.value.toTestCase(this)
         RootTest(tc, it.index)
      }
   }

   /**
    * Include the tests, listeners and extensions from the given [TestFactory] in this spec.
    * Tests are added in order from where this include was invoked using configuration and
    * settings at the time the method was invoked.
    */
   fun include(factory: TestFactory) {
      // todo factory.createTestCases(this::class.toDescription(), this).forEach { registerRootTest(it) }
      TODO()
      listeners(factory.listeners)
   }

   /**
    * Includes the tests from the given [TestFactory] in this spec or factory, with the given
    * prefixed added to each of the test's name.
    */
   fun include(prefix: String, factory: TestFactory) {
      include(factory.copy(tests = factory.tests.map { it.addPrefix(prefix) }))
   }

   /**
    * Registers a callback that will execute after all tests in this spec have completed.
    * This is a convenience method for creating a [TestListener] and registering it to only
    * fire for this spec.
    */
   fun finalizeSpec(f: FinalizeSpec) {
      configuration.registerListener(object : TestListener {
         override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
            if (kclass == this@DslDrivenSpec::class) {
               f(Tuple2(kclass, results))
            }
         }
      })
   }

   /**
    * Registers a callback that will execute after all specs have completed.
    * This is a convenience method for creating a [ProjectListener] and registering it.
    */
   fun afterProject(f: AfterProject) {
      configuration.registerListener(object : ProjectListener {
         override suspend fun afterProject() {
            f()
         }
      })
   }

   @Deprecated("this makes no sense")
   fun aroundSpec(aroundSpecFn: AroundSpecFn) {
      extension(object : SpecExtension {
         override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
            aroundSpecFn(Tuple2(spec, process))
         }
      })
   }

   override fun addTest(
      name: DescriptionName.TestName,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) = registerRootTest(name, test, config, type)

   /**
    * Adds a new root-level test to this [Spec] which will be inflated into a [TestCase]
    * by the framework when the spec is executed.
    */
   fun registerRootTest(
      name: DescriptionName.TestName,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      val rtc = RootTestCase(
         name = name,
         test = test,
         config = config,
         type = type,
         sourceRef = sourceRef(),
         source = source(),
      )
      rootTestCases = rootTestCases + rtc
   }
}

internal data class RootTestCase(
   @Deprecated("Will be removed")
   val name: DescriptionName.TestName,
   val test: suspend TestContext.() -> Unit,
   val config: TestCaseConfig,
   val type: TestType,
   @Deprecated("Will be removed")
   val sourceRef: SourceRef,
   val source: Source,
)

internal suspend fun RootTestCase.toTestCase(spec: Spec): TestCase {

   val tags = config.tags + spec.declaredTags() + spec::class.tags()

   val testCase = TestCase(
      description = spec::class.toDescription().append(name, type),
      spec = spec,
      test = test,
      source = sourceRef(),
      type = type,
      config = config,
      factoryId = null,
      assertionMode = null,
      parent = null, // root tests do not have a parent test case
   )

   val enabled = testCase.isEnabled()

   val node = spec::class.toNode().append(
      name = NodeName.fromTestName(this.name),
      type = type,
      source = source(),
      tags = tags,
      enabled = enabled,
      severity = config.severity,
   )

   val testCase2 = testCase.copy(node = node)

   return if (configuration.testNameAppendTags) {
      TestCase.appendTagsInDisplayName(testCase2)
   } else {
      testCase2
   }
}
