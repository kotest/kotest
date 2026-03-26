package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig
import kotlin.time.Duration

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

   suspend infix fun String.fwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.FOCUSED, init)

   suspend infix fun String.xwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.DISABLED, init)

   private suspend fun `when`(name: String, xmethod: TestXMethod, test: suspend WordSpecWhenContainerScope.() -> Unit) {
      registerContainer(
         name = TestNameBuilder.builder(name).withSuffix(" when").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null,
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

   private suspend fun should(
      name: String,
      test: suspend WordSpecShouldContainerScope.() -> Unit,
      xmethod: TestXMethod
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withSuffix(" should").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null
      ) { WordSpecShouldContainerScope(this).test() }
   }

   /**
    * Adds a configured test to this scope as a leaf test.
    *
    * E.g.
    * ```
    * "this test".config(...) `when` { }
    * ```
    */
   suspend fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      failfast: Boolean? = null,
      blockingTest: Boolean? = null,
      coroutineTestScope: Boolean? = null,
   ) : WordSpecContextConfigBuilder {
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

   fun String.config(
      config: TestConfig,
   ): WordSpecContextConfigBuilder {
      return WordSpecContextConfigBuilder(this, config)
   }

   @Suppress("FunctionName")
   suspend infix fun WordSpecContextConfigBuilder.When(test: suspend WordSpecWhenContainerScope.() -> Unit) =
      addWhen(this.name, TestXMethod.NONE, this.config, test)

   suspend infix fun WordSpecContextConfigBuilder.fWhen(test: suspend WordSpecWhenContainerScope.() -> Unit) =
      addWhen(this.name, TestXMethod.FOCUSED, this.config, test)

   suspend infix fun WordSpecContextConfigBuilder.xWhen(test: suspend WordSpecWhenContainerScope.() -> Unit) =
      addWhen(this.name, TestXMethod.DISABLED, this.config, test)

   suspend infix fun WordSpecContextConfigBuilder.`when`(test: suspend WordSpecWhenContainerScope.() -> Unit) =
      addWhen(this.name, TestXMethod.NONE, this.config, test)

   suspend infix fun WordSpecContextConfigBuilder.fwhen(test: suspend WordSpecWhenContainerScope.() -> Unit) =
      addWhen(this.name, TestXMethod.FOCUSED, this.config, test)

   suspend infix fun WordSpecContextConfigBuilder.xwhen(test: suspend WordSpecWhenContainerScope.() -> Unit) =
      addWhen(this.name, TestXMethod.DISABLED, this.config, test)

   private suspend fun addWhen(name: String, xmethod: TestXMethod, config: TestConfig?, test: suspend WordSpecWhenContainerScope.() -> Unit) =
      registerContainer(
         name = TestNameBuilder.builder(name).withSuffix(" when").build(),
         xmethod = xmethod,
         config = config
      ) { WordSpecWhenContainerScope(this).test() }



   suspend infix fun WordSpecContextConfigBuilder.should(test: suspend WordSpecShouldContainerScope.() -> Unit) =
      addShould(this.name, TestXMethod.NONE, this.config, test)

   suspend infix fun WordSpecContextConfigBuilder.fshould(test: suspend WordSpecShouldContainerScope.() -> Unit) =
      addShould(this.name, TestXMethod.FOCUSED, this.config, test)

   suspend infix fun WordSpecContextConfigBuilder.xshould(test: suspend WordSpecShouldContainerScope.() -> Unit) =
      addShould(this.name, TestXMethod.DISABLED, this.config, test)

   private suspend fun addShould(name: String, xmethod: TestXMethod, config: TestConfig?, test: suspend WordSpecShouldContainerScope.() -> Unit) {
      registerContainer(
         name = TestNameBuilder.builder(name).withSuffix(" when").build(),
         xmethod = xmethod,
         config = config
      ) { WordSpecShouldContainerScope(this).test() }
   }

}

