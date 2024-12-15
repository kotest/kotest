package io.kotest.core.extensions

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.test.Enabled

/**
 * An extension point that is used to override if a [Descriptor] is enabled or disabled.
 *
 * If multiple instances of this extension are defined then all must agree that a descriptor
 * is enabled, otherwise it will be disabled.
 */
interface EnabledExtension : Extension {

   /**
    * Invoked to override if a test or spec is enabled or disabled.
    *
    * This method can choose to override that status by returning:
    *  - [Enabled.enabled] if this test or spec should be enabled
    *  - [Enabled.disabled] if this test or spec should be disabled
    */
   suspend fun isEnabled(descriptor: Descriptor): Enabled
}
