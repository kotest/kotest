package io.kotest.core.test.config

import io.kotest.core.Tag
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestContainerConfig
import kotlin.time.Duration

/**
 * Creates and returns a new [TestContainerConfig] from the given parameters, using values
 * from the receiver as defaults.
 */
internal fun TestContainerConfig.deriveTestContainerConfig(
   enabled: Boolean? = null,
   enabledIf: EnabledIf? = null,
   tags: Set<Tag>? = null,
   timeout: Duration? = null,
) = TestContainerConfig(
   enabled = enabled ?: this.enabled,
   tags = tags ?: this.tags,
   timeout = timeout ?: this.timeout,
   enabledIf = enabledIf ?: this.enabledIf,
)
