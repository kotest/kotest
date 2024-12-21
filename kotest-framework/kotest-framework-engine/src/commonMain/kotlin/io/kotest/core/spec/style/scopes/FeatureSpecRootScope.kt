package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.engine.stable.StableIdents

/**
 * Extends [RootScope] with dsl-methods for the 'fun spec' style.
 *
 * E.g.:
 *
 * ```
 * feature("some context") { }
 * xfeature("some test") { }
 * ```
 */
interface FeatureSpecRootScope : RootScope {

   fun feature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) =
      addFeature(name = name, xdisabled = false, test = test)

   fun xfeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) =
      addFeature(name = name, xdisabled = true, test = test)

   @ExperimentalKotest
   fun feature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("Feature: ", name, false), false, this) { FeatureSpecContainerScope(it) }

   @ExperimentalKotest
   fun xfeature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("Feature: ", name, false), true, this) { FeatureSpecContainerScope(it) }

   fun addFeature(name: String, xdisabled: Boolean, test: suspend FeatureSpecContainerScope.() -> Unit) {
      val testName = TestName("Feature: ", name, false)
      addContainer(testName, xdisabled, null) { FeatureSpecContainerScope(this).test() }
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
      test: suspend FeatureSpecContainerScope.(T) -> Unit
   ) {
      withData(listOf(first, second) + rest, test)
   }

   fun <T> withData(
      nameFn: (T) -> String,
      first: T,
      second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
      vararg rest: T,
      test: suspend FeatureSpecContainerScope.(T) -> Unit
   ) {
      withData(nameFn, listOf(first, second) + rest, test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(ts: Sequence<T>, test: suspend FeatureSpecContainerScope.(T) -> Unit) {
      withData(ts.toList(), test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(nameFn: (T) -> String, ts: Sequence<T>, test: suspend FeatureSpecContainerScope.(T) -> Unit) {
      withData(nameFn, ts.toList(), test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(ts: Iterable<T>, test: suspend FeatureSpecContainerScope.(T) -> Unit) {
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
      test: suspend FeatureSpecContainerScope.(T) -> Unit
   ) {
      ts.forEach { t ->
         addFeature(nameFn(t), false) { this.test(t) }
      }
   }
}
