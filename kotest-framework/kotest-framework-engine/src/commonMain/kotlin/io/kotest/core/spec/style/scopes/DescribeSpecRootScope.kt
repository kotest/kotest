package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.test.TestScope
import io.kotest.engine.stable.StableIdents

/**
 * A context that allows root tests to be registered using the syntax:
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
 */
interface DescribeSpecRootScope : RootScope {

   fun context(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(TestName("Context: ", name, false), false, null) { DescribeSpecContainerScope(this).test() }
   }

   fun xcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(TestName("Context: ", name, false), true, null) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun context(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName(name), xdisabled = false, this) { DescribeSpecContainerScope(it) }

   @ExperimentalKotest
   fun xcontext(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName(name), xdisabled = true, this) { DescribeSpecContainerScope(it) }

   fun describe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestName("Describe: ", name, false),
         disabled = false,
         null
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun xdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestName("Describe: ", name, false),
         disabled = true,
         null
      ) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun describe(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestName("Describe: ", name, false),
         xdisabled = false,
         this
      ) { DescribeSpecContainerScope(it) }

   @ExperimentalKotest
   fun xdescribe(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestName("Describe: ", name, false),
         xdisabled = true,
         this
      ) { DescribeSpecContainerScope(it) }

   fun it(name: String, test: suspend TestScope.() -> Unit) {
      addTest(TestName(name), false, null, test)
   }

   fun xit(name: String, test: suspend TestScope.() -> Unit) {
      addTest(TestName(name), true, null, test)
   }

   // data-test DSL follows

   /**
    * Registers tests at the root level for each element.
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(
      first: T,
      second: T, // we need two elements here so the compiler can disambiguate from the sequence version
      vararg rest: T,
      test: suspend DescribeSpecContainerScope.(T) -> Unit
   ) {
      withData(listOf(first, second) + rest, test)
   }

   fun <T> withData(
      nameFn: (T) -> String,
      first: T,
      second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
      vararg rest: T,
      test: suspend DescribeSpecContainerScope.(T) -> Unit
   ) = withData(nameFn, listOf(first, second) + rest, test)

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(
      ts: Sequence<T>,
      test: suspend DescribeSpecContainerScope.(T) -> Unit
   ) {
      withData(ts.toList(), test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(
      nameFn: (T) -> String,
      ts: Sequence<T>, test: suspend DescribeSpecContainerScope.(T) -> Unit
   ) {
      withData(nameFn, ts.toList(), test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(
      ts: Iterable<T>,
      test: suspend DescribeSpecContainerScope.(T) -> Unit
   ) {
      withData({ StableIdents.getStableIdentifier(it) }, ts, test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the given [nameFn] function.
    */
   fun <T> withData(
      nameFn: (T) -> String,
      ts: Iterable<T>,
      test: suspend DescribeSpecContainerScope.(T) -> Unit
   ) {
      ts.forEach { t ->
         addContainer(
            TestName("Describe: ", nameFn(t), false),
            false,
            null
         ) { DescribeSpecContainerScope(this).test(t) }
      }
   }
}
