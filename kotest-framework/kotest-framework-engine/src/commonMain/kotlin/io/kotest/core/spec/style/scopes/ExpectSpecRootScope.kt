package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.test.TestScope
import io.kotest.engine.stable.StableIdents

/**
 * Top level registration methods for ExpectSpec methods.
 */
interface ExpectSpecRootScope : RootScope {

   fun context(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name, test, false)
   }

   fun xcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name, test, true)
   }

   /**
    * Adds a container test to this spec expecting config.
    */
   @ExperimentalKotest
   fun context(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("Context: ", name, false), false, this) { ExpectSpecContainerScope(it) }

   /**
    * Adds a disabled container test to this spec expecting config.
    */
   @ExperimentalKotest
   fun xcontext(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("Context: ", name, true), false, this) { ExpectSpecContainerScope(it) }

   fun expect(name: String, test: suspend TestScope.() -> Unit) {
      addTest(name, test, true)
   }

   fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      addTest(name, test, true)
   }

   fun expect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(this, TestName("Expect: ", name, null, false), false)
   }

   fun xexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(this, TestName("Expect: ", name, null, false), true)
   }

   private fun addContext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit, disabled: Boolean) {
      addContainer(
         testName = TestName("Context: ", name, false),
         disabled = disabled,
         config = null
      ) { ExpectSpecContainerScope(this).test() }
   }

   private fun addTest(name: String, test: suspend ExpectSpecContainerScope.() -> Unit, disabled: Boolean) {
      addTest(
         testName = TestName("Expect: ", name, false),
         disabled = disabled,
         config = null
      ) { ExpectSpecContainerScope(this).test() }
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
      test: suspend ExpectSpecContainerScope.(T) -> Unit
   ) {
      withData(listOf(first, second) + rest, test)
   }

   fun <T> withData(
      nameFn: (T) -> String,
      first: T,
      second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
      vararg rest: T,
      test: suspend ExpectSpecContainerScope.(T) -> Unit
   ) = withData(nameFn, listOf(first, second) + rest, test)

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(
      ts: Sequence<T>,
      test: suspend ExpectSpecContainerScope.(T) -> Unit
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
      ts: Sequence<T>, test: suspend ExpectSpecContainerScope.(T) -> Unit
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
      test: suspend ExpectSpecContainerScope.(T) -> Unit
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
      test: suspend ExpectSpecContainerScope.(T) -> Unit
   ) {
      ts.forEach { t ->
         addContext(nameFn(t), { this.test(t) }, false)
      }
   }
}
