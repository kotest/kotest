package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestType

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

   fun feature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(featureName(name), TestType.Container)
            .withXmethod(TestXMethod.NONE)
            .build { FeatureSpecContainerScope(this).test() }
      )
   }

   fun ffeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(featureName(name), TestType.Container)
            .withXmethod(TestXMethod.FOCUSED)
            .build { FeatureSpecContainerScope(this).test() }
      )
   }

   fun xfeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(featureName(name), TestType.Container)
            .withXmethod(TestXMethod.DISABLED)
            .build { FeatureSpecContainerScope(this).test() }
      )
   }

   fun feature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = featureName(name),
         xmethod = TestXMethod.NONE,
         context = this
      ) { FeatureSpecContainerScope(it) }

   fun ffeature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = featureName(name),
         xmethod = TestXMethod.FOCUSED,
         context = this
      ) { FeatureSpecContainerScope(it) }

   fun xfeature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = featureName(name),
         xmethod = TestXMethod.DISABLED,
         context = this
      ) { FeatureSpecContainerScope(it) }

   fun featureName(name: String): TestName = TestNameBuilder.builder(name).withPrefix("Feature: ").build()
}
