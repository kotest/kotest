package io.kotest.core.test

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.AbstractSpec
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.style.TestXMethod
import kotlinx.coroutines.CoroutineScope
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
   suspend fun registerTestCase(nested: NestedTest)
}

annotation class TestRunnable

abstract class SuiteSpec : AbstractSpec() {

   fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
      add(
         RootTest(
            name = TestNameBuilder.builder(name).build(),
            test = { SuiteScope(this).test() },
            type = TestType.Container,
            source = sourceRef(),
            xmethod = TestXMethod.NONE,
            config = null,
            factoryId = null
         )
      )
   }

   fun test(name: String, test: suspend TestScope.() -> Unit) {
      add(
         RootTest(
            name = TestNameBuilder.builder(name).build(),
            test = test,
            type = TestType.Test,
            source = sourceRef(),
            xmethod = TestXMethod.NONE,
            config = null,
            factoryId = null
         )
      )
   }
}

class SuiteScope(
   val testScope: TestScope,
) : DelegatingTestScope(testScope) {

   fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
      // testScope.registerTestCase(NestedTest(name, this))
   }

   fun test(name: String, test: suspend TestScope.() -> Unit) {
      // testScope.registerTestCase(NestedTest(name, this))
   }
}

abstract class DelegatingTestScope(private val testScope: TestScope) : TestScope {

   override val testCase: TestCase = testScope.testCase
   override val coroutineContext: CoroutineContext = testScope.coroutineContext

   override suspend fun registerTestCase(nested: NestedTest) {
      testScope.registerTestCase(nested)
   }
}
