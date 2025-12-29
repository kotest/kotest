package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.core.test.config.TestConfig

/**
 * A [RootScope] allows for [RootTest]s to be registered via a DSL.
 */
interface RootScope {
   /**
    * Register a new [RootTest].
    */
   fun add(test: RootTest)
}

/**
 * Convenience method to add a [TestType.Test] test to this [RootScope].
 */
fun RootScope.addTest(
   testName: TestName,
   xmethod: TestXMethod,
   config: TestConfig?,
   test: suspend TestScope.() -> Unit
) {
   add(
      RootTest(
         name = testName,
         test = test,
         type = TestType.Test,
         source = sourceRef(),
         xmethod = xmethod,
         config = config,
         factoryId = null,
      )
   )
}

/**
 * Convenience method to add a root test of type [TestType.Container] test to this [RootScope].
 */
fun RootScope.addContainer(
   testName: TestName,
   xmethod: TestXMethod,
   config: TestConfig?,
   test: suspend TestScope.() -> Unit
) {
   add(
      RootTest(
         name = testName,
         test = test,
         type = TestType.Container,
         source = sourceRef(),
         xmethod = xmethod,
         config = config,
         factoryId = null,
      )
   )
}
