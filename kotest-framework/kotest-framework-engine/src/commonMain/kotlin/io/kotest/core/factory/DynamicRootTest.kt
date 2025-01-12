package io.kotest.core.factory

import io.kotest.core.names.TestName
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.core.test.config.TestConfig

/**
 * A [DynamicRootTest] is an intermediate test state held by a factory. Once the factory is added to a
 * [Spec] and the spec is created, the factories dynamic tests will be added to the spec
 * as fully fledged [TestCase]s.
 */
data class DynamicRootTest(
   val name: TestName,
   val test: suspend TestScope.() -> Unit,
   val config: TestConfig,
   val type: TestType,
   val source: SourceRef,
   val factoryId: FactoryId
)
