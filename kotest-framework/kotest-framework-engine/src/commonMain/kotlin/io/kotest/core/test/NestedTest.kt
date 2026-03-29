package io.kotest.core.test

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestName
import io.kotest.core.source.SourceRef
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.config.TestConfig

/**
 * Describes a test that has been discovered at runtime but has not yet been
 * attached to a parent [TestCase].
 */
@KotestInternal
data class NestedTest(
   val name: TestName,
   val xmethod: TestXMethod,
   val config: TestConfig?, // can be null if the test does not specify config
   val type: TestType,
   val source: SourceRef,
   val test: suspend TestScope.() -> Unit,
)

data class NestedTestBuilder(
   val name: TestName,
   val test: suspend TestScope.() -> Unit,
   val type: TestType,
   val source: SourceRef,
   val xmethod: TestXMethod, // specifies if this test is being disabled or focused via a keyword such as xtest
   val config: TestConfig?, // if specified by the test, may be null if no config was explicitly set on the test itself
) {

   companion object {
      fun builder(name: TestName, type: TestType, test: suspend TestScope.() -> Unit): NestedTestBuilder {
         return NestedTestBuilder(name, test, type, sourceRef(), TestXMethod.NONE, null)
      }
   }

   fun withConfig(config: TestConfig): NestedTestBuilder = copy(config = config)
   fun withXmethod(xmethod: TestXMethod): NestedTestBuilder = copy(xmethod = xmethod)

   fun build(): NestedTest {
      return NestedTest(
         name = name,
         test = test,
         type = type,
         source = source,
         xmethod = xmethod,
         config = config,
      )
   }
}
