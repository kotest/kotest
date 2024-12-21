package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.spec.RootTest
import io.kotest.core.test.TestScope
import io.kotest.engine.stable.StableIdents

/**
 * Extends [RootScope] with dsl-methods for the 'fun spec' style.
 */
interface FunSpecRootScope : RootScope {

   /**
    * Adds a container [RootTest] that uses a [FunSpecContainerScope] as the test context.
    */
   fun context(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      addContainer(
         testName = TestName("context ", name, false),
         disabled = false,
         config = null
      ) { FunSpecContainerScope(this).test() }
   }

   /**
    * Adds a disabled container [RootTest] that uses a [FunSpecContainerScope] as the test context.
    */
   fun xcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) =
      addContainer(
         testName = TestName("context ", name, false),
         disabled = true,
         config = null
      ) { FunSpecContainerScope(this).test() }

   @ExperimentalKotest
   fun context(name: String): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("context ", name, false), false, this) { FunSpecContainerScope(it) }

   @ExperimentalKotest
   fun xcontext(name: String): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("context ", name, false), true, this) { FunSpecContainerScope(it) }

   /**
    * Adds a [RootTest], with the given name and config taken from the config builder.
    */
   fun test(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(this, TestName(name), xdisabled = false)

   /**
    * Adds a [RootTest], with the given name and default config.
    */
   fun test(name: String, test: suspend TestScope.() -> Unit) {
      addTest(testName = TestName(name), disabled = false, config = null, test = test)
   }

   /**
    * Adds a disabled [RootTest], with the given name and default config.
    */
   fun xtest(name: String, test: suspend TestScope.() -> Unit) {
      addTest(testName = TestName(name), disabled = true, config = null, test = test)
   }

   /**
    * Adds a disabled [RootTest], with the given name and with config taken from the config builder.
    */
   fun xtest(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(this, TestName(name), xdisabled = true)

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
      test: suspend FunSpecContainerScope.(T) -> Unit
   ) {
      withData(listOf(first, second) + rest, test)
   }

   fun <T> withData(
      nameFn: (T) -> String,
      first: T,
      second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
      vararg rest: T,
      test: suspend FunSpecContainerScope.(T) -> Unit
   ) = withData(nameFn, listOf(first, second) + rest, test)

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(ts: Sequence<T>, test: suspend FunSpecContainerScope.(T) -> Unit) {
      withData(ts.toList(), test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(nameFn: (T) -> String, ts: Sequence<T>, test: suspend FunSpecContainerScope.(T) -> Unit) {
      withData(nameFn, ts.toList(), test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(ts: Iterable<T>, test: suspend FunSpecContainerScope.(T) -> Unit) {
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
      test: suspend FunSpecContainerScope.(T) -> Unit
   ) {
      ts.forEach { t ->
         addContainer(TestName("context ", nameFn(t), false), false, null) { FunSpecContainerScope(this).test(t) }
      }
   }

   /**
    * Registers tests at the root level for each tuple of [data], with the first value of the tuple
    * used as the test name, and the second value passed to the test.
    */
   fun <T> withData(data: Map<String, T>, test: suspend FunSpecContainerScope.(T) -> Unit) {
      data.forEach { (name, t) ->
         addContainer(TestName("context ", name, false), false, null) { FunSpecContainerScope(this).test(t) }
      }
   }
}
