package io.kotest.core.test

import io.kotest.core.SourceRef
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.factory.FactoryId
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec

/**
 * A [TestCase] describes a test lambda at runtime.
 *
 * It contains a reference back to the [Spec] instance in which it
 * is being executed.
 *
 * It also captures a closure of the body of the test case.
 * This is a function which is invoked with a [TestContext].
 * The context is used so that the test function can, at runtime,
 * register nested tests with the test engine. This allows
 * nested tests to be executed lazily as required, rather
 * than when the [Spec] instance is created.
 *
 * A test can be nested inside other tests if the [Spec] supports it.
 *
 * For example, in the FunSpec we only allow top level tests.
 *
 * test("this is a test") { }
 *
 * And in WordSpec we allow two levels of tests.
 *
 * "a string" should {
 *   "return the length" {
 *   }
 * }
 *
 */
data class TestCase(
   val descriptor: Descriptor.TestDescriptor,
   val name: TestName,
   // the spec instance that contains this testcase
   val spec: Spec,
   // a closure of the test function
   val test: suspend TestContext.() -> Unit,
   val source: SourceRef,
   val type: TestType,
   // config used when running the test, such as number of
   // invocations, threads, etc
   val config: TestCaseConfig = TestCaseConfig(),
   // an optional factory id which is used to indicate which factory (if any) generated this test case.
   val factoryId: FactoryId? = null,
   // not null if this test has a parent test
   val parent: TestCase? = null,
) {

   @Deprecated("Use testCase.name or testCase.descriptor. This was deprecated in 5.0.")
   val displayName: String = descriptor.id.value

   init {
      if (type == TestType.Test && config.failfast == true) error("Cannot set fail fast on leaf test")
   }

   companion object {

      /**
       * Creates a [TestCase] of type [TestType.Test], with default config, and derived source ref.
       */
      fun test(
         descriptor: Descriptor.TestDescriptor,
         name: TestName,
         spec: Spec,
         parent: TestCase?,
         test: suspend TestContext.() -> Unit
      ): TestCase =
         TestCase(
            descriptor = descriptor,
            name = name,
            spec = spec,
            test = test,
            source = sourceRef(),
            type = TestType.Test,
            config = TestCaseConfig(),
            factoryId = null,
            parent = parent,
         )

      /**
       * Creates a [TestCase] of type [TestType.Container], with default config, and derived source ref.
       */
      fun container(
         descriptor: Descriptor.TestDescriptor,
         name: TestName,
         spec: Spec,
         parent: TestCase?,
         test: suspend TestContext.() -> Unit
      ): TestCase = TestCase(
         descriptor = descriptor,
         name = name,
         spec = spec,
         test = test,
         source = sourceRef(),
         type = TestType.Container,
         config = TestCaseConfig(),
         factoryId = null,
         parent = parent,
      )
   }
}

/**
 * Returns true if this descriptor represents a root test case.
 *
 * A root test case is one which is defined at the top level in a spec.
 */
fun TestCase.isRootTest() = this.parent == null
