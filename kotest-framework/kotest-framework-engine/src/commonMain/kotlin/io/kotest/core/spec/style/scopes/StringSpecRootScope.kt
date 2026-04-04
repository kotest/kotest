package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

/**
 * Defines the DSL for creating tests in the 'StringSpec' style.
 *
 * Example:
 * ```
 * "my test" {
 *   1 + 1 shouldBe 2
 * }
 * ```
 */
interface StringSpecRootScope : RootScope {

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
      enabledOrReasonIf: EnabledOrReasonIf? = null,
      coroutineDebugProbes: Boolean? = null,
      blockingTest: Boolean? = null,
      coroutineTestScope: Boolean? = null,
      test: suspend TestScope.() -> Unit,
   ) {
      RootTestWithConfigBuilder(
         context = this@StringSpecRootScope,
         name = TestNameBuilder.builder(this).build(),
         xmethod = TestXMethod.NONE
      ).config(
         enabled = enabled,
         enabledIf = enabledIf,
         enabledOrReasonIf = enabledOrReasonIf,
         invocations = invocations,
         timeout = timeout,
         invocationTimeout = invocationTimeout,
         tags = tags ?: emptySet(),
         extensions = extensions,
         severity = severity,
         coroutineDebugProbes = coroutineDebugProbes,
         coroutineTestScope = coroutineTestScope,
         blockingTest = blockingTest,
         test = test
      )
   }

   /**
    * Adds a String Spec test using the default test case config.
    */
   operator fun String.invoke(test: suspend StringSpecScope.() -> Unit) {
      addTest(
         testName = TestNameBuilder.builder(this).build(),
         xmethod = TestXMethod.NONE,
         config = null,
      ) {
         StringSpecScope(this).test()
      }
   }

   /**
    * Adds a String Spec test using the supplied [TestConfig].
    */
   fun String.config(config: TestConfig, test: suspend TestScope.() -> Unit) {
      addTest(
         testName = TestNameBuilder.builder(this).build(),
         xmethod = TestXMethod.NONE,
         config = config,
      ) {
         StringSpecScope(this).test()
      }
   }
}

/**
 * This scope exists purely to stop nested string scopes.
 */
@KotestTestScope
class StringSpecScope(
   testScope: TestScope
) : TerminalScope() {
   override val testCase: TestCase = testScope.testCase
   override val coroutineContext: CoroutineContext = testScope.coroutineContext
}
