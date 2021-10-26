package io.kotest.core.spec

import io.kotest.core.Tuple2
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.factory.TestFactory
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.scopes.RootContext
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.config.ResolvedTestConfig
import kotlin.reflect.KClass

/**
 * Base class for specs that allow for registration of tests via the DSL.
 */
abstract class DslDrivenSpec : Spec(), RootContext {

   /**
    * Contains the [RootTest]s that have been registered on this spec.
    */
   private var rootTests = emptyList<RootTest>()

   private val globalExtensions = mutableListOf<Extension>()

   override fun rootTests(): List<RootTest> {
      return rootTests
   }

   override fun globalExtensions(): List<Extension> {
      return globalExtensions.toList()
   }

   override fun add(test: RootTest) {
      rootTests = rootTests + test
   }

   /**
    * Include the tests and extensions from the given [TestFactory] in this spec.
    * Tests are added in order from where this include was invoked using configuration and
    * settings at the time the method was invoked.
    */
   fun include(factory: TestFactory) {
      factory.tests.forEach { add(it.copy(factoryId = factory.factoryId)) }
      register(factory.extensions)
   }

   /**
    * Includes the tests from the given [TestFactory] in this spec or factory, with the given
    * prefixed added to each of the test's name.
    */
   fun include(prefix: String, factory: TestFactory) {
      val renamed = factory.tests.map { test ->
         val name = test.name.copy(testName = prefix + test.name.testName)
         test.copy(name = name)
      }
      include(factory.copy(tests = renamed))
   }

   /**
    * Registers a callback that will execute after all tests in this spec have completed.
    *
    * This is a convenience method for creating a [FinalizeSpecListener] and constraining
    * it to only fire for this spec.
    */
   fun finalizeSpec(f: FinalizeSpec) {
      globalExtensions.add(object : FinalizeSpecListener {
         override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
            if (kclass == this@DslDrivenSpec::class) {
               f(Tuple2(kclass, results))
            }
         }
      })
   }

   /**
    * Registers a callback that will execute after all specs have completed.
    *
    * This is a convenience method for creating a [ProjectListener] and registering
    * it with project configuration.
    */
   fun afterProject(f: AfterProject) {
      globalExtensions.add(object : ProjectListener {
         override suspend fun afterProject() {
            f()
         }
      })
   }

   @Deprecated("This has no effect and will be removed in 6.0", level = DeprecationLevel.ERROR)
   fun aroundSpec(aroundSpecFn: AroundSpecFn) {
      extension(object : SpecExtension {
         override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
            aroundSpecFn(Tuple2(spec, process))
         }
      })
   }

   /**
    * Adds a new root-level [TestCase] to this [Spec].
    */
   override fun addTest(name: TestName, test: suspend TestContext.() -> Unit, config: ResolvedTestConfig, type: TestType) {
      rootTests = rootTests + RootTest(
         name = name,
         test = test,
         source = sourceRef(),
         type = type,
         config = null, // TODO(),
         disabled = false,
         factoryId = null,
      )
   }
//
//   /**
//    * Adds a [RootTest] to this [Spec].
//    */
//   private fun addRootTest(testCase: TestCase) {
//      val uniqueName = UniqueNames.unique(
//         testCase.name.testName,
//         rootTestCases.map { it.name.testName }.toSet()
//      )
//      val tc = if (uniqueName == null) testCase else testCase.copy(name = testCase.name.copy(testName = uniqueName))
//      rootTestCases = rootTestCases + tc
//   }
}
