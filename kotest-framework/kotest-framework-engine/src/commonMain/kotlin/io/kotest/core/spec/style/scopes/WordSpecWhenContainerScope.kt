package io.kotest.core.spec.style.scopes

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig

@Suppress("FunctionName")
@KotestTestScope
class WordSpecWhenContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   @Suppress("FunctionName")
   suspend infix fun String.When(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.NONE, init)

   suspend infix fun String.fWhen(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.FOCUSED, init)

   suspend infix fun String.xWhen(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.DISABLED, init)

   suspend infix fun String.`when`(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.NONE, init)

   /**
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   suspend fun String.`when`(config: TestConfig, init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.NONE, init, config)

   suspend infix fun String.fwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.FOCUSED, init)

   suspend infix fun String.xwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.DISABLED, init)

   private suspend fun `when`(name: String, xmethod: TestXMethod, test: suspend WordSpecWhenContainerScope.() -> Unit, config: TestConfig? = null) {
      registerContainer(
         name = TestNameBuilder.builder(name).withSuffix(" when").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = config,
      ) { WordSpecWhenContainerScope(this).test() }
   }

   suspend infix fun String.Should(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, test = test, xmethod = TestXMethod.NONE)
   }

   suspend infix fun String.should(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, test = test, xmethod = TestXMethod.NONE)
   }

   suspend infix fun String.fshould(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, test = test, xmethod = TestXMethod.FOCUSED)
   }

   suspend infix fun String.xshould(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, test = test, xmethod = TestXMethod.DISABLED)
   }

   /**
    * Adds a test with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   suspend fun String.should(config:TestConfig, test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, test = test, xmethod = TestXMethod.NONE, config = config)
   }

   private suspend fun should(
      name: String,
      test: suspend WordSpecShouldContainerScope.() -> Unit,
      xmethod: TestXMethod,
      config: TestConfig? = null
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withSuffix(" should").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = config
      ) { WordSpecShouldContainerScope(this).test() }
   }
}

