package io.kotest.core.test.config

import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf

/**
 * The spec DSLs allow the enabled flag to be specified in multiple ways due to historic reasons.
 * This method combines them to a single [EnabledOrReasonIf] function.
 */
fun enabledOrReasonIf(
   enabled: Boolean?,
   enabledIf: EnabledIf?,
   enabledOrReasonIf: EnabledOrReasonIf?
): EnabledOrReasonIf = { testCase ->
   when {
      enabled == false -> Enabled.disabled
      enabledIf != null && !enabledIf(testCase) -> Enabled.disabled
      enabledOrReasonIf != null -> enabledOrReasonIf(testCase)
      else -> Enabled.enabled
   }
}
