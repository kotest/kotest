package io.kotest.core.extensions

import io.kotest.core.config.ExperimentalKotest
import io.kotest.core.plan.Descriptor
import io.kotest.core.test.IsActive

/**
 * An extension point that is used to override if a [Descriptor] is inactive or active.
 *
 * If multiple instances of this extension are defined then all will be executed and all must respond active.
 */
@ExperimentalKotest
interface IsActiveExtension : Extension {

   /**
    * Invoked to override if a test or spec is active or inactive.
    *
    * This method can choose to override that status by returning:
    *  - true if this test or spec should be active regardless of the input active status
    *  - false if this test or spec should be inactive regardless of the input active status
    */
   suspend fun isActive(descriptor: Descriptor): IsActive
}
