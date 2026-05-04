package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * ```
 * describe("some test")
 * ```
 *
 * or
 *
 * ```
 * xdescribe("some disabled test")
 * ```
 *
 * and
 *
 * ```
 * it("some test")
 * it("some test").config(...)
 * xit("some test")
 * xit("some test").config(...)
 * ```
 */
@KotestTestScope
class DescribeSpecContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   /**
    * Registers a container test.
    */
   suspend fun context(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.NONE, test = test)
   }

   /**
    * Registers a disabled container test.
    */
   suspend fun xcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   private suspend fun context(
      name: String,
      xmethod: TestXMethod,
      test: suspend DescribeSpecContainerScope.() -> Unit
   ) {
      registerTest(
         TestDefinitionBuilder
            .builder(contextName(name), TestType.Container)
            .withXmethod(xmethod)
            .build { DescribeSpecContainerScope(this).test() }
      )
   }

   fun context(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         name = contextName(name),
         context = this,
         xmethod = TestXMethod.NONE,
      ) { DescribeSpecContainerScope(it) }

   fun fcontext(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         name = contextName(name),
         context = this,
         xmethod = TestXMethod.FOCUSED,
      ) { DescribeSpecContainerScope(it) }


   fun xcontext(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         name = contextName(name),
         context = this,
         xmethod = TestXMethod.DISABLED,
      ) { DescribeSpecContainerScope(it) }

   /**
    * Registers a container test.
    */
   suspend fun describe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder.builder(describeName(name), TestType.Container)
            .withXmethod(TestXMethod.NONE)
            .build { DescribeSpecContainerScope(this).test() }
      )
   }

   /**
    * Registers a container test.
    */
   suspend fun xdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder.builder(describeName(name), TestType.Container)
            .withXmethod(TestXMethod.DISABLED)
            .build { DescribeSpecContainerScope(this).test() }
      )
   }

   fun describe(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         name = describeName(name),
         context = this,
         xmethod = TestXMethod.NONE,
      ) { DescribeSpecContainerScope(it) }

   fun xdescribe(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         name = describeName(name),
         context = this,
         xmethod = TestXMethod.DISABLED,
      ) { DescribeSpecContainerScope(it) }

   suspend fun it(name: String): TestWithConfigBuilder {
      TestDslState.startTest(itName(name))
      return TestWithConfigBuilder(
         name = itName(name),
         context = this,
         xmethod = TestXMethod.NONE,
      )
   }

   suspend fun xit(name: String): TestWithConfigBuilder {
      TestDslState.startTest(itName(name))
      return TestWithConfigBuilder(
         itName(name),
         this,
         xmethod = TestXMethod.DISABLED,
      )
   }

   suspend fun it(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder.builder(itName(name), TestType.Test).build(test)
      )
   }

   suspend fun fit(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder.builder(itName(name), TestType.Test)
            .withXmethod(TestXMethod.FOCUSED)
            .build(test)
      )
   }

   suspend fun xit(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder.builder(itName(name), TestType.Test)
            .withXmethod(TestXMethod.DISABLED)
            .build(test)
      )
   }

   private fun describeName(name: String) =
      TestNameBuilder.builder(name).withPrefix("Describe: ").build()

   private fun itName(name: String) =
      TestNameBuilder.builder(name).withPrefix("It: ").withDefaultAffixes().build()

   private fun contextName(name: String) =
      TestNameBuilder.builder(name).withPrefix("Context: ").build()
}
