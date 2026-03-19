package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.config.TestConfig
import kotlin.time.Duration

data class WordSpecContextConfigBuilder(val name: String, val config: TestConfig)

interface WordSpecRootScope : RootScope {

   infix fun String.should(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, xmethod = TestXMethod.NONE, test = test)
   }

   infix fun String.fshould(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, xmethod = TestXMethod.FOCUSED, test = test)
   }

   infix fun String.xshould(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, xmethod = TestXMethod.DISABLED, test = test)
   }

   private fun should(name: String, xmethod: TestXMethod, test: suspend WordSpecShouldContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withSuffix(" should").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null,
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
      test: suspend WordSpecWhenContainerScope.() -> Unit
   ) {
      addContainer(
         testName = TestNameBuilder.builder(name).withSuffix(" when").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null,
      ) { WordSpecWhenContainerScope(this).test() }
   }

   /**
    * Starts a config builder, which can be added to the scope by invoking [`when`] or [should] on the returned value.
    *
    * E.g.
    *
    * ```
    * "this test".config(...) `when` { }
    * ```
    */
   fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      failfast: Boolean? = null,
      blockingTest: Boolean? = null,
      coroutineTestScope: Boolean? = null,
   ): WordSpecContextConfigBuilder {
      val config = TestConfig(
         enabled = enabled,
         tags = tags ?: emptySet(),
         extensions = extensions,
         timeout = timeout,
         invocationTimeout = invocationTimeout,
         enabledIf = enabledIf,
         invocations = invocations,
         severity = severity,
         failfast = failfast,
         blockingTest = blockingTest,
         coroutineTestScope = coroutineTestScope,
      )
      return config(config)
   }

   /**
    * Starts a config builder, which can be added to the scope by invoking [`when`] or [should] on the returned value.
    *
    * E.g.
    *
    * ```
    * "this test".config(...) `when` { }
    * ```
    */
   fun String.config(
      config: TestConfig,
   ): WordSpecContextConfigBuilder {
      return WordSpecContextConfigBuilder(this, config)
   }

   @Suppress("FunctionName")
   infix fun WordSpecContextConfigBuilder.When(test: suspend WordSpecWhenContainerScope.() -> Unit) =
      addWhen(this.name, TestXMethod.NONE, this.config, test)

   infix fun WordSpecContextConfigBuilder.fWhen(test: suspend WordSpecWhenContainerScope.() -> Unit) =
      addWhen(this.name, TestXMethod.FOCUSED, this.config, test)

   infix fun WordSpecContextConfigBuilder.xWhen(test: suspend WordSpecWhenContainerScope.() -> Unit) =
      addWhen(this.name, TestXMethod.DISABLED, this.config, test)

   infix fun WordSpecContextConfigBuilder.`when`(test: suspend WordSpecWhenContainerScope.() -> Unit) =
      addWhen(this.name, TestXMethod.NONE, this.config, test)

   infix fun WordSpecContextConfigBuilder.fwhen(test: suspend WordSpecWhenContainerScope.() -> Unit) =
      addWhen(this.name, TestXMethod.FOCUSED, this.config, test)

   infix fun WordSpecContextConfigBuilder.xwhen(test: suspend WordSpecWhenContainerScope.() -> Unit) =
      addWhen(this.name, TestXMethod.DISABLED, this.config, test)

   private fun addWhen(name: String, xmethod: TestXMethod, config: TestConfig?, test: suspend WordSpecWhenContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withSuffix(" when").build(),
         xmethod = xmethod,
         config = config
      ) { WordSpecWhenContainerScope(this).test() }
   }


   infix fun WordSpecContextConfigBuilder.should(test: suspend WordSpecShouldContainerScope.() -> Unit) =
      addShould(this.name, TestXMethod.NONE, this.config, test)

   infix fun WordSpecContextConfigBuilder.fshould(test: suspend WordSpecShouldContainerScope.() -> Unit) =
      addShould(this.name, TestXMethod.FOCUSED, this.config, test)

   infix fun WordSpecContextConfigBuilder.xshould(test: suspend WordSpecShouldContainerScope.() -> Unit) =
      addShould(this.name, TestXMethod.DISABLED, this.config, test)

   private fun addShould(name: String, xmethod: TestXMethod, config: TestConfig?, test: suspend WordSpecShouldContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withSuffix(" when").build(),
         xmethod = xmethod,
         config = config
      ) { WordSpecShouldContainerScope(this).test() }
   }
}
