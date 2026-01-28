package io.kotest.core.spec.style.scopes

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig

/**
 * A context that allows tests to be registered using the syntax:
 *
 * ```
 * when("some test")
 * when("some test").config(...)
 * xwhen("some disabled test")
 * xwhen("some disabled test").config(...)
 * ```
 *
 * and
 *
 * ```
 * then("some test")
 * then("some test").config(...)
 * xthen("some disabled test").config(...)
 * xthen("some disabled test").config(...)
 * ```
 */
@Suppress("FunctionName")
@KotestTestScope
class BehaviorSpecGivenContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   suspend fun And(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xmethod = TestXMethod.NONE, test)

   suspend fun and(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xmethod = TestXMethod.NONE, test)

   /**
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   suspend fun and(name: String, config: TestConfig, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xmethod = TestXMethod.NONE, test, config)

   suspend fun xand(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xmethod = TestXMethod.DISABLED, test)

   suspend fun xAnd(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xmethod = TestXMethod.DISABLED, test)

   private suspend fun addAnd(
      name: String,
      xmethod: TestXMethod,
      test: suspend BehaviorSpecGivenContainerScope.() -> Unit,
      config: TestConfig? = null
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("And: ").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = config
      ) {
         BehaviorSpecGivenContainerScope(this).test()
      }
   }

   suspend fun When(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xmethod = TestXMethod.NONE)

   suspend fun `when`(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xmethod = TestXMethod.NONE)

   /**
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   suspend fun `when`(name: String, config: TestConfig, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xmethod = TestXMethod.NONE, config = config)

   suspend fun xwhen(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xmethod = TestXMethod.DISABLED)

   suspend fun xWhen(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xmethod = TestXMethod.DISABLED)

   private suspend fun addWhen(
      name: String,
      test: suspend BehaviorSpecWhenContainerScope.() -> Unit,
      xmethod: TestXMethod,
      config: TestConfig? = null
   ) {
      registerContainer(TestNameBuilder.builder(name).withPrefix("When: ").withDefaultAffixes().build(), xmethod = xmethod, config) {
         BehaviorSpecWhenContainerScope(this).test()
      }
   }

   suspend fun When(name: String) =
      addWhen(name, xmethod = TestXMethod.NONE)

   suspend fun `when`(name: String) =
      addWhen(name, xmethod = TestXMethod.NONE)

   suspend fun xwhen(name: String) =
      addWhen(name, xmethod = TestXMethod.DISABLED)

   suspend fun xWhen(name: String) =
      addWhen(name, xmethod = TestXMethod.DISABLED)

   suspend fun fwhen(name: String) =
      addWhen(name, xmethod = TestXMethod.FOCUSED)

   suspend fun fWhen(name: String) =
      addWhen(name, xmethod = TestXMethod.FOCUSED)

   private suspend fun addWhen(
      name: String,
      xmethod: TestXMethod
   ): ContainerWithConfigBuilder<BehaviorSpecWhenContainerScope> {
      return ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("When: ").withDefaultAffixes().build(),
         context = this,
         xmethod = xmethod
      ) { BehaviorSpecWhenContainerScope(it) }
   }

   fun Then(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecGivenContainerScope,
      xmethod = TestXMethod.NONE
   )

   fun then(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecGivenContainerScope,
      xmethod = TestXMethod.NONE
   )

   fun xthen(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecGivenContainerScope,
      xmethod = TestXMethod.DISABLED
   )

   fun xThen(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecGivenContainerScope,
      xmethod = TestXMethod.DISABLED
   )

   suspend fun Then(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xmethod = TestXMethod.NONE)
   suspend fun then(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xmethod = TestXMethod.NONE)

   /**
    * Adds a test with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   suspend fun then(name: String, config: TestConfig, test: suspend TestScope.() -> Unit) =
      addThen(name, test, xmethod = TestXMethod.NONE, config = config)

   suspend fun xthen(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xmethod = TestXMethod.DISABLED)
   suspend fun xThen(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xmethod = TestXMethod.DISABLED)

   private suspend fun addThen(name: String, test: suspend TestScope.() -> Unit, xmethod: TestXMethod, config: TestConfig? = null) {
      registerTest(
         TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
         xmethod = xmethod,
         config,
         test
      )
   }
}
