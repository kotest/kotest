package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType

/**
 * A context that allows tests to be registered using the syntax:
 *
 * ```
 * context("some test")
 * xcontext("some disabled test")
 * ```
 *
 * and
 *
 * ```
 * expect("some test")
 * expect("some test").config(...)
 * xexpect("some test")
 * xexpect("some test").config(...)
 * ```
 */
@KotestTestScope
class ExpectSpecContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   suspend fun context(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder.builder(contextName(name), TestType.Container)
            .withXmethod(TestXMethod.NONE)
            .build { ExpectSpecContainerScope(this).test() }
      )
   }

   suspend fun xcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder.builder(contextName(name), TestType.Container)
            .withXmethod(TestXMethod.DISABLED)
            .build { ExpectSpecContainerScope(this).test() }
      )
   }

   fun context(name: String) =
      ContainerWithConfigBuilder(
         name = contextName(name),
         context = this,
         xmethod = TestXMethod.NONE,
      ) { ExpectSpecContainerScope(it) }

   fun xcontext(name: String) =
      ContainerWithConfigBuilder(
         name = contextName(name),
         context = this,
         xmethod = TestXMethod.DISABLED,
      ) { ExpectSpecContainerScope(it) }

   suspend fun expect(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder.builder(expectName(name), TestType.Test).build(test)
      )
   }

   suspend fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder.builder(expectName(name), TestType.Test)
            .withXmethod(TestXMethod.DISABLED)
            .build(test)
      )
   }

   suspend fun expect(name: String): TestWithConfigBuilder {
      val testName = expectName(name)
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.NONE,
      )
   }

   suspend fun xexpect(name: String): TestWithConfigBuilder {
      val testName = expectName(name)
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.DISABLED,
      )
   }

   private fun expectName(name: String): TestName = TestNameBuilder.builder(name).withPrefix("Expect: ").build()
   private fun contextName(name: String): TestName = TestNameBuilder.builder(name).withPrefix("Context: ").build()
}

