package io.kotest.engine.preconditions

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import kotlin.reflect.KClass

/**
 * Validates that a [Spec] class is compatible on the actual platform. For example, in JS we can only
 * support certain spec styles due to limitations in the underlying test runners. As another example,
 * we may throw an error if an unsupported annotation is used.
 */
typealias ValidateSpec = (KClass<*>) -> Unit

// validate that this spec is suitable for platforms that don't support nesting of tests
val IsNotNestedSpecStyle: ValidateSpec = { it is FunSpec || it is StringSpec || it is ShouldSpec }

/**
 * Validates that a [TestCase] is compatible on the actual platform. For example, in JS we can only
 * support certain spec styles due to limitations in the underlying test runners.
 */
typealias ValidateTestCase = (TestCase) -> Unit
