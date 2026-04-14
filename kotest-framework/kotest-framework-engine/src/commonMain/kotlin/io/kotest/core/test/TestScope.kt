package io.kotest.core.test

import io.kotest.common.KotestInternal
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.TestDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.CoroutineContext

/**
 * A test in Kotest is simply a function `suspend TestScope.() -> Unit`
 *
 * The [TestScope] receiver allows the test function to interact with the test engine at runtime.
 * For instance fetching details of the executing [TestCase] (such as timeouts, tags) or by
 * registering a dynamic nested test.
 *
 * This context extends [CoroutineScope] giving the ability for any test to launch
 * coroutines directly, without requiring the user to supply a coroutine scope, and to retrieve
 * elements from the current [CoroutineContext] via [CoroutineContext.get].
 */
@KotestTestScope
interface TestScope : CoroutineScope {

   /**
    * The currently executing [TestCase].
    */
   val testCase: TestCase

   /**
    * Registers a [NestedTest] with the engine as a child of the current [testCase].
    *
    * Will throw an error if the current [testCase] is not a container test.
    *
    * This method is not intended for use directly, but by spec styles which use this to support their DSL.
    */
   @KotestInternal
   @Deprecated("Use registerTest with TestDefinitionBuilder. Deprecated in 6.2. Will be removed in 7.0")
   suspend fun registerTestCase(nested: NestedTest)

   /**
    * Registers a [TestDefinition] with the engine as a child of the current [testCase].
    *
    * Will throw an error if the current [testCase] is not a container test.
    *
    * This method is not intended for use directly, but by spec styles which use this to support their DSL.
    */
   suspend fun registerTest(test: TestDefinition) {
      @Suppress("DEPRECATION")
      registerTestCase(
         NestedTest(
            name = test.name,
            config = test.config,
            type = test.type,
            test = test.test,
            source = test.source,
            xmethod = test.xmethod,
         )
      )
   }
}

@KotestInternal
class DefaultTestScope(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext,
   private val onRegister: suspend (NestedTest) -> Unit,
) : TestScope {

   @Deprecated("Use registerTest(TestDefinition) instead. Deprecated in 6.2. Will be removed in 7.0")
   override suspend fun registerTestCase(nested: NestedTest) {
      onRegister(nested)
   }

   override suspend fun registerTest(test: TestDefinition) {
      onRegister(
         NestedTest(
            name = test.name,
            config = test.config,
            type = test.type,
            test = test.test,
            source = test.source,
            xmethod = test.xmethod,
         )
      )
   }

   companion object {
      suspend operator fun invoke(testCase: TestCase, onRegister: suspend (NestedTest) -> Unit): TestScope {
         return DefaultTestScope(testCase, currentCoroutineContext(), onRegister)
      }
   }
}

