package io.kotest.core.test

import io.kotest.core.SourceRef
import io.kotest.core.config.ExperimentalKotest
import io.kotest.core.factory.FactoryId
import io.kotest.core.internal.tags.allTags
import io.kotest.core.plan.Descriptor
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec

/**
 * A [TestCase] describes an actual block of code that will be tested.
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
   // the description contains the names of all parents, plus the name of this test case
   val description: Description.Test,
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
   // assertion mode can be set to control errors/warnings in a test
   // if null, defaults will be applied
   val assertionMode: AssertionMode? = null,

   // only set for scripts
   @ExperimentalKotest val descriptor: Descriptor.TestDescriptor? = null,

   // not null if this test has a parent test
   @ExperimentalKotest val parent: TestCase? = null,
) {

   val displayName = description.displayName()

   /**
    * Returns true if this test case is a root test inside a spec.
    */
   @Deprecated("use description.isRootTest(). Will be removed in 4.5")
   fun isTopLevel(): Boolean = description.isRootTest()

   companion object {

      /**
       * Creates a [TestCase] of type [TestType.Test], with default config, and derived source ref.
       */
      fun test(
         description: Description.Test,
         spec: Spec,
         parent: TestCase?,
         test: suspend TestContext.() -> Unit
      ): TestCase =
         TestCase(
            description = description,
            spec = spec,
            test = test,
            source = sourceRef(),
            type = TestType.Test,
            config = TestCaseConfig(),
            factoryId = null,
            assertionMode = null,
            parent = parent,
         )

      /**
       * Creates a [TestCase] of type [TestType.Container], with default config, and derived source ref.
       */
      fun container(
         description: Description.Test,
         spec: Spec,
         parent: TestCase?,
         test: suspend TestContext.() -> Unit
      ): TestCase = TestCase(
         description = description,
         spec = spec,
         test = test,
         source = sourceRef(),
         type = TestType.Container,
         config = TestCaseConfig(),
         factoryId = null,
         assertionMode = null,
         parent = parent,
      )

      fun appendTagsInDisplayName(testCase: TestCase): TestCase {
         val tagNames = testCase.allTags().joinToString(", ")

         return if (tagNames.isNotBlank()) {
            val description = testCase.description
            val originalName = description.name
            val nameWithTagsAppended = originalName.copy(displayName = "${originalName.displayName}[tags = $tagNames]")
            testCase.copy(description = description.copy(name = nameWithTagsAppended))
         } else {
            testCase
         }
      }
   }
}
