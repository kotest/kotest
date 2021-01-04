package io.kotest.core.extensions

import io.kotest.core.test.TestNode

/**
 * An extension point that is used to override if a [TestNode.TestCaseNode] is inactive or active.
 *
 * If multiple instances are defined then all will be executed, but the order is not specified.
 */
interface TestActiveExtension : Extension {

   /**
    * Invoked to override if a test is active or inactive.
    *
    * The provided node will have it's active status set depending on the default rules or
    * as modified by any previously executed extensions.
    *
    * This method can choose to override that status by returning:
    *  - true if this test should be active regardless of the input active status
    *  - false if this test should be inactive regardless of the input active status
    */
   suspend fun isActive(node: TestNode.TestCaseNode): Boolean
}
