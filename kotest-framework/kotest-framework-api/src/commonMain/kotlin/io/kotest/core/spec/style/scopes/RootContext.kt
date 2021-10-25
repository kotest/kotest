package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.RootTest
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.config.UnresolvedTestConfig

@Deprecated("Renamed to RootContext. Deprecated since 4.5.")
typealias RootScope = RootContext

/**
 * A root context allows for [RootTest]s to be registered via a DSL.
 */
interface RootContext {
   /**
    * Register a new [RootTest].
    */
   fun add(test: RootTest)
}

/**
 * Convenience method to add a test type [RootTest] that uses default config.
 */
fun RootContext.addTest(
   testName: TestName,
   disabled: Boolean,
   config: UnresolvedTestConfig?,
   test: suspend TestContext.() -> Unit
) {
   add(
      RootTest(
         name = testName,
         test = test,
         type = TestType.Test,
         source = sourceRef(),
         disabled = disabled,
         config = config,
         factoryId = null,
      )
   )
}

/**
 * Convenience method to add a container type [RootTest] that uses default config.
 *
 * This method wraps the [TestContext] with [IncompleteContainerContext] to detect
 * when no nested tests are added, and if so, throw an exception.
 */
fun RootContext.addContainer(
   testName: TestName,
   disabled: Boolean,
   config: UnresolvedTestConfig?,
   test: suspend IncompleteContainerContext.() -> Unit
) {
   add(
      RootTest(
         name = testName,
         test = {
            IncompleteContainerContext(this).apply {
               this.test()
               if (!hasNestedTest) throw IncompleteContainerException(testName.testName)
            }
         },
         type = TestType.Container,
         source = sourceRef(),
         disabled = disabled,
         config = config,
         factoryId = null,
      )
   )
}
