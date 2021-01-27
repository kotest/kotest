package io.kotest.core.extensions

import io.kotest.core.config.ExperimentalKotest
import io.kotest.core.plan.TestPlanNode

/**
 * An extension point that is used to override if a [TestPlanNode] is inactive or active.
 *
 * If multiple instances of this extension are defined then all will be executed, but the order is not specified.
 */
@ExperimentalKotest
interface IsActiveExtension : Extension {

   /**
    * Invoked to override if a test or spec is active or inactive.
    *
    * The provided node will have it's active status set depending on the default rules or
    * as modified by any previously executed extensions.
    *
    * This method can choose to override that status by returning:
    *  - true if this test or spec should be active regardless of the input active status
    *  - false if this test or spec should be inactive regardless of the input active status
    */
   suspend fun isActive(node: TestPlanNode): Boolean
}
