package io.kotest.core.spec.style.scopes

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.config.TestConfig

interface WordSpecRootScope : RootScope {

   infix fun String.should(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, xmethod = TestXMethod.NONE, test = test)
   }

   /**
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   fun String.should(config: TestConfig, test: suspend WordSpecShouldContainerScope.() -> Unit) =
      should(name = this, xmethod = TestXMethod.NONE, test = test, config = config)

   infix fun String.fshould(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, xmethod = TestXMethod.FOCUSED, test = test)
   }

   infix fun String.xshould(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, xmethod = TestXMethod.DISABLED, test = test)
   }

   private fun should(name: String, xmethod: TestXMethod, test: suspend WordSpecShouldContainerScope.() -> Unit, config: TestConfig? = null) {
      addContainer(
         testName = TestNameBuilder.builder(name).withSuffix(" should").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = config,
      ) { WordSpecShouldContainerScope(this).test() }
   }

   infix fun String.When(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xmethod = TestXMethod.NONE,
      test = init
   )

   infix fun String.fWhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xmethod = TestXMethod.FOCUSED,
      test = init
   )

   infix fun String.xWhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xmethod = TestXMethod.DISABLED,
      test = init
   )

   infix fun String.`when`(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xmethod = TestXMethod.NONE,
      test = init
   )

   /**
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   fun String.`when`(config: TestConfig, init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.NONE, init, config)

   infix fun String.fwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xmethod = TestXMethod.FOCUSED,
      test = init
   )

   infix fun String.xwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xmethod = TestXMethod.DISABLED,
      test = init
   )

   private fun `when`(
      name: String,
      xmethod: TestXMethod,
      test: suspend WordSpecWhenContainerScope.() -> Unit,
      config: TestConfig? = null,
   ) {
      addContainer(
         testName = TestNameBuilder.builder(name).withSuffix(" when").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = config,
      ) { WordSpecWhenContainerScope(this).test() }
   }
}
