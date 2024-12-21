package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.stable.StableIdents
import kotlin.jvm.JvmName

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * ```
 * describe("some test")
 * ```
 *
 * or
 *
 * ```
 * xdescribe("some disabled test")
 * ```
 *
 * and
 *
 * ```
 * it("some test")
 * it("some test").config(...)
 * xit("some test")
 * xit("some test").config(...)
 * ```
 */
@KotestTestScope
class DescribeSpecContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   /**
    * Registers a container test.
    */
   suspend fun context(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      registerContainer(TestName("Context: ", name, false), false, null) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun context(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(TestName(name), this, false) { DescribeSpecContainerScope(it) }

   /**
    * Registers a disabled container test.
    */
   suspend fun xcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      registerContainer(TestName("Context: ", name, false), true, null) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun xcontext(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(TestName("Context: ", name, false), this, true) { DescribeSpecContainerScope(it) }

   /**
    * Registers a container test.
    */
   suspend fun describe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      registerContainer(TestName("Describe: ", name, false), false, null) { DescribeSpecContainerScope(this).test() }
   }

   /**
    * Registers a container test.
    */
   suspend fun xdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      registerContainer(TestName("Describe: ", name, false), true, null) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun describe(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         TestName("Describe: ", name, false),
         this,
         false
      ) { DescribeSpecContainerScope(it) }

   @ExperimentalKotest
   fun xdescribe(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         TestName("Describe: ", name, false),
         this,
         true
      ) { DescribeSpecContainerScope(it) }

   suspend fun it(name: String): TestWithConfigBuilder {
     TestDslState.startTest(name)
      return TestWithConfigBuilder(
         TestName("It: ", name, false),
         this,
         xdisabled = false,
      )
   }

   suspend fun xit(name: String): TestWithConfigBuilder {
     TestDslState.startTest(name)
      return TestWithConfigBuilder(
         TestName("It: ", name, false),
         this,
         xdisabled = true,
      )
   }

   suspend fun it(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(TestName(name), false, null) { DescribeSpecContainerScope(this).test() }
   }

   suspend fun xit(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(TestName(name), true, null) { DescribeSpecContainerScope(this).test() }
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
      test: suspend DescribeSpecContainerScope.(T) -> Unit
   ) = withData(listOf(first, second) + rest, test)

   /**
    * Registers tests inside the given test context for each element of [ts].
    * The test names will be generated from the stable properties of the elements. See [StableIdents].
    */
   suspend fun <T> withData(
      ts: Sequence<T>,
      test: suspend DescribeSpecContainerScope.(T) -> Unit
   ) = withData(ts.toList(), test)

   /**
    * Registers tests inside the given test context for each element of [ts].
    * The test names will be generated from the stable properties of the elements. See [StableIdents].
    */
   suspend fun <T> withData(
      ts: Iterable<T>,
      test: suspend DescribeSpecContainerScope.(T) -> Unit
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
      test: suspend DescribeSpecContainerScope.(T) -> Unit
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
      test: suspend DescribeSpecContainerScope.(T) -> Unit
   ) = withData(nameFn, listOf(first, second) + rest, test)

   /**
    * Registers tests inside the given [DescribeSpecContainerScope] for each element of [ts].
    * The test name will be generated from the given [nameFn] function.
    */
   suspend fun <T> withData(
      nameFn: (T) -> String,
      @BuilderInference ts: Iterable<T>,
      @BuilderInference test: suspend DescribeSpecContainerScope.(T) -> Unit
   ) {
      ts.forEach { t ->
         registerTest(TestName(nameFn(t)), false, null, TestType.Container) { DescribeSpecContainerScope(this).test(t) }
      }
   }

   /**
    * Registers tests inside the given test context for each tuple of [data], with the first value
    * of the tuple used as the test name, and the second value passed to the test.
    */
   @JvmName("withDataMap")
   suspend fun <T> withData(data: Map<String, T>, test: suspend DescribeSpecContainerScope.(T) -> Unit) {
      data.forEach { (name, t) ->
         registerTest(TestName(name), false, null, TestType.Container) { DescribeSpecContainerScope(this).test(t) }
      }
   }
}
