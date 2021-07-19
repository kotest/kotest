package io.kotest.core.factory

import io.kotest.core.plan.Source
import io.kotest.core.plan.TestName
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

/**
 * A [DynamicRootTest] is an intermediate test state held by a factory. Once the factory is added to a
 * [Spec] and the spec is created, the factory's dynamic tests will be added to the spec
 * as fully fledged [TestCase]s.
 */
data class DynamicRootTest(
   val name: TestName,
   val test: suspend TestContext.() -> Unit,
   val config: TestCaseConfig,
   val type: TestType,
   val source: Source?,
   val factoryId: FactoryId
)

fun DynamicRootTest.addPrefix(prefix: String): DynamicRootTest {
   return copy(name = this.name.copy(testName = "$prefix ${this.name.testName}"))
}
