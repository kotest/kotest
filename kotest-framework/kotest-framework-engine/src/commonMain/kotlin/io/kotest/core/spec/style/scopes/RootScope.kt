@file:Suppress("DEPRECATION")

package io.kotest.core.spec.style.scopes

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestName
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.TestDefinition
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.core.test.config.TestConfig

/**
 * A [RootScope] allows for top level [TestDefinition]s to be registered via a DSL.
 */
@KotestInternal
interface RootScope {
   /**
    * Register a new [RootTest].
    */
   @Deprecated("Use add(TestDefinition). Deprecated since 6.2. Will be removed in 7.0")
   fun add(test: RootTest)

   fun add(test: TestDefinition)
}

/**
 * Convenience method to add a [TestType.Test] test to this [RootScope].
 */
@Deprecated("Use addTest with TestXMethod parameter. Deprecated since 6.1. Will be removed in 7.0")
fun RootScope.addTest(
   testName: TestName,
   disabled: Boolean,
   config: TestConfig?,
   test: suspend TestScope.() -> Unit
) {
   add(
      TestDefinitionBuilder.builder(testName, TestType.Test)
         .withXmethod(if (disabled) TestXMethod.DISABLED else TestXMethod.NONE)
         .withConfig(config)
         .build(test)
   )
}

/**
 * Convenience method to add a [TestType.Test] test to this [RootScope].
 */
@Deprecated("Use add directly on RootScope with TestDefinitionBuilder. Deprecated since 6.2. Will be removed in 7.0")
fun RootScope.addTest(
   testName: TestName,
   xmethod: TestXMethod,
   config: TestConfig?,
   test: suspend TestScope.() -> Unit
) {
   add(
      TestDefinitionBuilder.builder(testName, TestType.Test)
         .withXmethod(xmethod)
         .withConfig(config)
         .build(test)
   )
}

/**
 * Convenience method to add a root test of type [TestType.Container] test to this [RootScope].
 */
@Deprecated("Use add directly on RootScope with TestDefinitionBuilder. Deprecated since 6.2. Will be removed in 7.0")
fun RootScope.addContainer(
   testName: TestName,
   xmethod: TestXMethod,
   config: TestConfig?,
   test: suspend TestScope.() -> Unit
) {
   add(
      TestDefinitionBuilder.builder(testName, TestType.Container)
         .withXmethod(xmethod)
         .withConfig(config)
         .build(test)
   )
}
