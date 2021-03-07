package io.kotest.core.test

import io.kotest.core.Tag
import kotlin.time.Duration

/**
 * Contains config that is applicable to parent/container tests.
 */
data class TestContainerConfig(

   /**
    * If set to false, this test and any nested tests will be disabled.
    */
   val enabled: Boolean = true,

   /**
    * If this function evaluates to false, then this test and any nested tests will be disabled.
    */
   val enabledIf: EnabledIf = { true },

   /**
    * A timeout that applies to this test and any nested tests.
    * Note that at any level, a test and any children must complete within that timeout value.
    * Nested tests can set their own timeout value which will apply to their segment of the test tree.
    */
   val timeout: Duration? = null,

   /**
    * [Tag]s that are applied to this test case and nested child tests.
    */
   val tags: Set<Tag> = emptySet(),
)

fun TestContainerConfig.toTestConfig() =
   TestCaseConfig(
      enabled = this.enabled,
      enabledIf = this.enabledIf,
      tags = this.tags,
      timeout = this.timeout,
      invocationTimeout = null,
      listeners = emptyList(),
      extensions = emptyList()
   )
