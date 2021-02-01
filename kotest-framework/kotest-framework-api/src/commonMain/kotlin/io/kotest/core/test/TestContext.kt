package io.kotest.core.test

import io.kotest.core.plan.Descriptor
import io.kotest.core.sourceRef
import io.kotest.core.spec.KotestDsl
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

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
    *
    * This will throw if we are trying to add a nested test to a non-container.
    */
   suspend fun registerTestCase(
      name: DescriptionName.TestName,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType,
      descriptor: Descriptor.TestDescriptor? = null,
   ) {
      when (testCase.type) {
         TestType.Container -> {
            val nested = NestedTest(name, test, config, type, sourceRef(), testCase.factoryId, descriptor)
            registerTestCase(nested)
         }
         TestType.Test -> throw InvalidTestConstructionException("Cannot add a nested test to '${testCase.displayName}' because it is not a test container")
      }
   }

   /**
    * Notifies the test runner about a test in a nested scope.
    */
   suspend fun registerTestCase(nested: NestedTest)
}

class InvalidTestConstructionException(msg: String) : RuntimeException(msg)

private class TestContextWithCoroutineContext(
   val delegate: TestContext,
   override val coroutineContext: CoroutineContext
) : TestContext by delegate {
   override fun toString(): String = "TestCaseContext [$coroutineContext]"
}

/**
 * Returns a new [TestContext] which uses the given [coroutineContext] with the other methods
 * delegating to the receiver context.
 */
internal fun TestContext.withCoroutineContext(coroutineContext: CoroutineContext): TestContext =
   if (this is TestContextWithCoroutineContext) {
      TestContextWithCoroutineContext(delegate, coroutineContext)
   } else {
      TestContextWithCoroutineContext(this, coroutineContext)
   }
