package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.stable.StableIdents
import kotlin.jvm.JvmName

/**
 * A context that allows tests to be registered using the syntax:
 *
 * context("some context")
 * test("some test")
 * test("some test").config(...)
 *
 */
@KotestTestScope
class FunSpecContainerScope(
   testScope: TestScope,
) : AbstractContainerScope(testScope) {

   /**
    * Adds a 'context' container test as a child of the current test case.
    */
   suspend fun context(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      registerContainer(TestName(name), false, null) { FunSpecContainerScope(this).test() }
   }

   /**
    * Adds a container test to this context expecting config.
    */
   @ExperimentalKotest
   fun context(name: String): ContainerWithConfigBuilder<FunSpecContainerScope> {
      return ContainerWithConfigBuilder(
         name = TestName(name),
         context = this,
         xdisabled = false,
         contextFn = { FunSpecContainerScope(it) }
      )
   }

   /**
    * Adds a disabled container test to this context.
    */
   suspend fun xcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      registerContainer(TestName(name), true, null) { FunSpecContainerScope(this).test() }
   }

   /**
    * Adds a disabled container to this context, expecting config.
    */
   @ExperimentalKotest
   fun xcontext(name: String): ContainerWithConfigBuilder<FunSpecContainerScope> {
      return ContainerWithConfigBuilder(
         TestName(name),
         this,
         true
      ) { FunSpecContainerScope(it) }
   }

   /**
    * Adds a test case to this context, expecting config.
    */
   suspend fun test(name: String): TestWithConfigBuilder {
      TestDslState.startTest(name)
      return TestWithConfigBuilder(
         name = TestName(name),
         context = this,
         xdisabled = false,
      )
   }

   /**
    * Adds a disabled test case to this context, expecting config.
    */
   suspend fun xtest(name: String): TestWithConfigBuilder {
      TestDslState.startTest(name)
      return TestWithConfigBuilder(
         name = TestName(name),
         context = this,
         xdisabled = true,
      )
   }

   /**
    * Adds a test case to this context.
    */
   suspend fun test(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(TestName(name), false, null, test)
   }

   /**
    * Adds a disabled test case to this context.
    */
   suspend fun xtest(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(TestName(name), true, null, test)
   }

   // data-test DSL follows

   /**
    * Registers tests inside the given test context for each element.
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   suspend fun <T> withData(
      first: T,
      second: T, // we need second to help the compiler disambiguate between this and the sequence version
      vararg rest: T,
      test: suspend FunSpecContainerScope.(T) -> Unit
   ) = withData(listOf(first, second) + rest, test)

   /**
    * Registers tests inside the given test context for each element of [ts].
    * The test names will be generated from the stable properties of the elements. See [StableIdents].
    */
   suspend fun <T> withData(
      ts: Sequence<T>,
      test: suspend FunSpecContainerScope.(T) -> Unit
   ) = withData(ts.toList(), test)

   /**
    * Registers tests inside the given test context for each element of [ts].
    * The test names will be generated from the stable properties of the elements. See [StableIdents].
    */
   suspend fun <T> withData(
      ts: Iterable<T>,
      test: suspend FunSpecContainerScope.(T) -> Unit
   ) {
      withData({ StableIdents.getStableIdentifier(it) }, ts, test)
   }

   /**
    * Registers tests inside the given test context for each element of [ts].
    * The test name will be generated from the given [nameFn] function.
    */
   suspend fun <T> withData(
      nameFn: (T) -> String,
      ts: Sequence<T>,
      test: suspend FunSpecContainerScope.(T) -> Unit
   ) = withData(nameFn, ts.toList(), test)

   /**
    * Registers tests inside the given test context for each element.
    * The test name will be generated from the given [nameFn] function.
    */
   suspend fun <T> withData(
      nameFn: (T) -> String,
      first: T,
      second: T,
      vararg rest: T,
      test: suspend FunSpecContainerScope.(T) -> Unit
   ) = withData(nameFn, listOf(first, second) + rest, test)

   /**
    * Registers tests inside the given [FunSpecContainerScope] for each element of [ts].
    * The test name will be generated from the given [nameFn] function.
    */
   suspend fun <T> withData(
      nameFn: (T) -> String,
      @BuilderInference ts: Iterable<T>,
      @BuilderInference test: suspend FunSpecContainerScope.(T) -> Unit
   ) {
      ts.forEach { t ->
         registerTest(TestName(nameFn(t)), false, null, TestType.Container) { FunSpecContainerScope(this).test(t) }
      }
   }

   /**
    * Registers tests inside the given test context for each tuple of [data], with the first value
    * of the tuple used as the test name, and the second value passed to the test.
    */
   @JvmName("withDataMap")
   suspend fun <T> withData(data: Map<String, T>, test: suspend FunSpecContainerScope.(T) -> Unit) {
      data.forEach { (name, t) ->
         registerTest(TestName(name), false, null, TestType.Container) { FunSpecContainerScope(this).test(t) }
      }
   }
}
