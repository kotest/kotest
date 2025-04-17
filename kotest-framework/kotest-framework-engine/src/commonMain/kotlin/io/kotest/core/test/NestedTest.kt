package io.kotest.core.test

import io.kotest.core.names.TestName
import io.kotest.core.source.SourceRef
import io.kotest.core.test.config.TestConfig

/**
 * Describes a test that has been discovered at runtime but has not yet been
 * attached to a parent [TestCase].
 */
data class NestedTest(
   val name: TestName,
   val disabled: Boolean,
   val config: TestConfig?, // can be null if the test does not specify config
   val type: TestType,
   val source: SourceRef,
   val test: suspend TestScope.() -> Unit,
)
