package io.kotest.core.test

import io.kotest.core.factory.FactoryId
import io.kotest.core.plan.Source
import io.kotest.core.plan.TestName
import io.kotest.core.source

/**
 * Describes a test that has been discovered at runtime but has not yet been
 * attached to a parent test.
 */
data class NestedTest(
   val name: TestName,
   val test: suspend TestContext.() -> Unit,
   val config: TestCaseConfig,
   val type: TestType,
   val source: Source?,
   val factoryId: FactoryId?,
)

/**
 * Creates a new [NestedTest] from the given parameters, with source evaluated from the
 * execution point, and [xdisabled] applied to the config.
 */
fun createNestedTest(
   name: TestName,
   xdisabled: Boolean,
   config: TestCaseConfig,
   type: TestType,
   factoryId: FactoryId?,
   test: suspend TestContext.() -> Unit,
) = NestedTest(
   name = name,
   test = test,
   config = if (xdisabled) config.copy(enabled = false) else config,
   type = type,
   source = source(),
   factoryId = factoryId,
)
