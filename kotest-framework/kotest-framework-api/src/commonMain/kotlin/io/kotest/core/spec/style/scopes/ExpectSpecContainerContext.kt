package io.kotest.core.spec.style.scopes

import io.kotest.core.descriptors.append
import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

@Deprecated("This interface has been renamed to ExpectSpecContainerContext. Deprecated since 4.5.")
typealias ExpectScope = ExpectSpecContainerContext

/**
 * A context that allows tests to be registered using the syntax:
 *
 * context("some test")
 * xcontext("some disabled test")
 *
 * and
 *
 * expect("some test")
 * expect("some test").config(...)
 * xexpect("some test")
 * xexpect("some test").config(...)
 *
 */
@KotestDsl
class ExpectSpecContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> context(name, test)
         TestType.Test -> expect(name, test)
      }
   }

   suspend fun context(name: String, test: suspend ExpectSpecContainerContext.() -> Unit) {
      registerContainer(TestName("Context: ", name, false), false, null) { ExpectSpecContainerContext(this).test() }
   }

   suspend fun xcontext(name: String, test: suspend ExpectSpecContainerContext.() -> Unit) {
      registerContainer(TestName("Context: ", name, false), true, null) { ExpectSpecContainerContext(this).test() }
   }

   suspend fun expect(name: String, test: suspend TestContext.() -> Unit) {
      registerTest(TestName("Expect: ", name, false), false, null, test)
   }

   suspend fun xexpect(name: String, test: suspend TestContext.() -> Unit) {
      registerTest(TestName("Expect: ", name, false), true, null, test)
   }

   suspend fun expect(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.append(name))
      return TestWithConfigBuilder(
         name = TestName("Expect: ", name, false),
         context = this,
         xdisabled = false,
      )
   }

   suspend fun xexpect(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.append(name))
      return TestWithConfigBuilder(
         name = TestName("Expect: ", name, false),
         context = this,
         xdisabled = true,
      )
   }
}
