package io.kotest.core.test

import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.TestDefinition
import kotlin.coroutines.CoroutineContext

/**
 * Base class for creating custom test scope implementations when building custom spec styles.
 *
 * Extend this class to create scope objects that expose your DSL methods inside container tests.
 * Use [registerTest] to register nested tests and containers within a running container.
 *
 * @see io.kotest.core.spec.AbstractSpec for the root-level spec base class.
 */
@KotestTestScope
abstract class AbstractTestScope(private val delegate: TestScope) : TestScope {

   override val testCase: TestCase = delegate.testCase
   override val coroutineContext: CoroutineContext = delegate.coroutineContext

   @Deprecated("Use registerTest with TestDefinitionBuilder. Deprecated in 6.2. Will be removed in 7.0")
   override suspend fun registerTestCase(nested: NestedTest) {
      @Suppress("DEPRECATION")
      delegate.registerTestCase(nested)
   }

   override suspend fun registerTest(test: TestDefinition) {
      delegate.registerTest(test)
   }
}
