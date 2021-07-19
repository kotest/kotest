package io.kotest.core.extensions

import io.kotest.common.ExperimentalKotest
import io.kotest.core.plan.Descriptor
import io.kotest.core.test.Enabled

/**
 * An extension point that is used to override if a [TestDescriptor] is enabled or disabled.
 *
 * If multiple instances of this extension are defined then all will be executed and all must respond enabled.
 */
@ExperimentalKotest
interface EnabledExtension : Extension {

   /**
    * Invoked to override if a test or spec is enabled or disabled.
    *
    * This method can choose to override that status by returning:
    *  - [Enabled.enabled] if this test or spec should be enabled regardless of the input enabled status
    *  - [Enabled.disabled] if this test or spec should be disabled regardless of the input enabled status
    */
   suspend fun isEnabled(descriptor: Descriptor.TestDescriptor): Enabled
}
