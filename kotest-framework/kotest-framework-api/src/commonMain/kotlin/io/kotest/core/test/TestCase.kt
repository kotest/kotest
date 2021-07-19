package io.kotest.core.test

import io.kotest.core.Tag
import io.kotest.core.factory.FactoryId
import io.kotest.core.internal.tags.tags
import io.kotest.core.plan.Descriptor
import io.kotest.core.plan.Source
import io.kotest.core.source
import io.kotest.core.spec.Spec

/**
 * A [TestCase] is a container for a test function at runtime.
 *
 * It contains a reference back to the [Spec] instance in which it
 * is being executed.
 *
 * It captures the closure of the body of the test case.
 * This is a function which is invoked with a [TestContext].
 *
 * The context is used so that the test function can, at runtime,
 * register nested tests with the test engine. This allows
 * nested tests to be executed lazily as required, rather
 * than when the [Spec] instance is created.
 *
 * A test can be nested inside other tests if the spec style supports it.
 *
 * For example, in the FunSpec we can nest inside 'context' block.
 *
 * contest("this is a parent test") {
 *   test("this is a nested test") { }
 * }
 *
 */
data class TestCase(
   // a handle to test id and naming
   val descriptor: Descriptor.TestDescriptor,
   // the spec instance that contains this testcase
   val spec: Spec,
   // the parent of this test if needed, is null for a top level test
   val parent: TestCase?,
   // the test function
   val test: suspend TestContext.() -> Unit,
   // if this test permits nested tests
   val type: TestType,
   // contains a reference to where this test case was defined in code
   val source: Source?,
   // config used when running the test, such as number of
   // invocations, number threads, is the test disabled, and so on
   val config: TestCaseConfig = TestCaseConfig(),
   // an optional factory id which is used to indicate which factory (if any) generated this test case.
   val factoryId: FactoryId? = null,
) {

   val displayName = descriptor.displayName

   /**
    * Returns all [Tag]s applicable to this this case, taken from those defined on
    * the [TestCaseConfig] or the [Spec] itself.
    */
   fun tags(): Set<Tag> = config.tags + spec.declaredTags() + spec::class.tags()

   companion object {

      /**
       * Creates a [TestCase] of type [TestType.Test], with default config, given parent, and derived source.
       */
      fun test(
         descriptor: Descriptor.TestDescriptor,
         spec: Spec,
         parent: TestCase? = null,
         test: suspend TestContext.() -> Unit
      ): TestCase = TestCase(
         descriptor = descriptor,
         spec = spec,
         parent = parent,
         test = test,
         type = TestType.Test,
         source = source(),
         config = TestCaseConfig(),
         factoryId = null,
      )

      /**
       * Creates a [TestCase] of type [TestType.Container], with default config, given parent, and derived source.
       */
      fun container(
         descriptor: Descriptor.TestDescriptor,
         spec: Spec,
         parent: TestCase? = null,
         test: suspend TestContext.() -> Unit
      ): TestCase = TestCase(
         descriptor = descriptor,
         spec = spec,
         parent = parent,
         test = test,
         type = TestType.Container,
         source = source(),
         config = TestCaseConfig(),
         factoryId = null,
      )
   }
}
