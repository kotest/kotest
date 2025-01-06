package io.kotest.core.test

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.factory.FactoryId
import io.kotest.core.names.TestName
import io.kotest.core.source.SourceRef
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.test.config.TestConfig
import kotlin.time.Duration

/**
 * A [TestCase] describes a test lambda at runtime.
 *
 * It contains a reference back to the [Spec] instance in which it
 * is being executed.
 *
 * It also captures a closure of the body of the test case.
 * This is a function which is invoked with a [TestScope].
 * The context is used so that the test function can, at runtime,
 * register nested tests with the test engine. This allows
 * nested tests to be executed lazily as required, rather
 * than when the [Spec] instance is created.
 *
 * A test can be nested inside other tests if the [Spec] supports it.
 *
 * For example, in the FunSpec we only allow top level tests.
 *
 * ```
 * test("this is a test") { }
 * ```
 *
 * And in WordSpec we allow two levels of tests.
 *
 * ```
 * "a string" should {
 *   "return the length" {
 *   }
 * }
 * ```
 */
data class TestCase(
   // parseable, stable, consistent identifier for this test element
   val descriptor: Descriptor.TestDescriptor,
   // the name of the test as entered by the user
   val name: TestName,
   // the spec instance that contains this testcase
   val spec: Spec,
   // a closure of the test function
   val test: suspend TestScope.() -> Unit,
   // a reference to the source code where this test case was defined
   val source: SourceRef = sourceRef(),
   // the type specifies if this test case is permitted to contain nested tests (container)
   val type: TestType,
   // config values specified directly on the test itself
   val config: TestConfig? = null,
   // an optional factory id which is used to indicate which factory (if any) generated this test case.
   val factoryId: FactoryId? = null,
   // the parent test case for this test at runtime, or null
   val parent: TestCase? = null,
)

/**
 * Returns `true` if this descriptor represents a root test case.
 *
 * A root test case is one which is defined at the top level in a spec.
 */
fun TestCase.isRootTest() = this.parent == null

fun TestCase.parents(): List<TestCase> {
   return if (parent == null) emptyList() else parent.parents() + parent
}

/** Returns timeout to be used depending on the [TestType]. */
val TestCase.timeout: Duration
   get() = when (type) {
      TestType.Container -> config?.timeout ?: TODO() // todo must resolve this
      else -> TODO() // todo must resolve this
      // minOf(config.invocationTimeout, config.timeout)
   }
