package io.kotest.core.test

import io.kotest.core.SourceRef
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.KotestDsl
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
abstract class TestContext : CoroutineScope {

   // this is added to stop the string spec from allowing nested tests
   infix operator fun String.invoke(@Suppress("UNUSED_PARAMETER") ignored: suspend TestContext.() -> Unit) {
      throw Exception("Nested tests are not allowed to be defined here. Please see the documentation for the spec styles")
   }

   /**
    * The currently executing [TestCase].
    */
   abstract val testCase: TestCase

   /**
    * Creates a [NestedTest] and then registers with the [TestContext].
    */
   suspend fun registerTestCase(
      name: TestName,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      val nested = NestedTest(name, test, config, type, sourceRef())
      registerTestCase(nested)
   }

   /**
    * Notifies the test runner about a test in a nested scope.
    */
   abstract suspend fun registerTestCase(nested: NestedTest)
}

data class NestedTest(
   val name: TestName,
   val test: suspend TestContext.() -> Unit,
   val config: TestCaseConfig,
   val type: TestType,
   val sourceRef: SourceRef
)

fun NestedTest.toTestCase(spec: Spec, parent: Description): TestCase {
   return TestCase(
      parent.append(this.name),
      spec,
      test,
      sourceRef,
      type,
      config,
      null,
      null
   )
}
