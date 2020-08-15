package io.kotest.core.test

import io.kotest.core.sourceRef
import io.kotest.core.spec.KotestDsl
import kotlinx.coroutines.CoroutineScope

/**
 * A [TestContext] is used as the receiver of a closure that is associated with a [TestCase].
 *
 * This allows the scope body to interact with the test engine, for instance, adding metadata
 * during a test, inspecting the current [TestCaseConfig], or notifying the runtime of a nested test.
 *
 * The test context extends [CoroutineScope] giving the ability for any test closure to launch
 * coroutines directly, without requiring them to create a scope.
 */
@KotestDsl
interface TestContext : CoroutineScope {

   // this is added to stop the string spec from allowing nested tests
   infix operator fun String.invoke(@Suppress("UNUSED_PARAMETER") ignored: suspend TestContext.() -> Unit) {
      throw Exception("Nested tests are not allowed to be defined here. Please see the documentation for the spec styles")
   }

   /**
    * The currently executing [TestCase].
    */
   val testCase: TestCase

   /**
    * Creates a [NestedTest] and then registers with the [TestContext].
    */
   suspend fun registerTestCase(
      name: DescriptionName.TestName,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      val nested = NestedTest(name, test, config, type, sourceRef(), testCase.factoryId)
      registerTestCase(nested)
   }

   /**
    * Notifies the test runner about a test in a nested scope.
    */
   suspend fun registerTestCase(nested: NestedTest)
}
