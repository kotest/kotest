package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestScope
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

@Deprecated("Renamed to StringSpecRootScope. Deprecated since 5.0")
typealias StringSpecRootContext = StringSpecRootScope

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
      test: suspend TestScope.() -> Unit,
   ) {
      RootTestWithConfigBuilder(
         this@StringSpecRootScope,
         TestName(null, this, false),
         false
      ).config(
         enabled = enabled,
         invocations = invocations,
         threads = threads,
         tags = tags,
         timeout = timeout,
         extensions = extensions,
         enabledIf = enabledIf,
         invocationTimeout = invocationTimeout,
         severity = severity,
         enabledOrReasonIf = enabledOrReasonIf,
         coroutineDebugProbes = coroutineDebugProbes,
         blockingTest = blockingTest,
         test = test
      )
   }

   /**
    * Adds a String Spec test using the default test case config.
    */
   operator fun String.invoke(test: suspend StringSpecScope.() -> Unit) {
      addTest(TestName(null, this, false), false, null) {
         StringSpecScope(this.coroutineContext, testCase).test()
      }
   }
}

/**
 * This scope exists purely to stop nested string scopes.
 */
@KotestTestScope
class StringSpecScope(
   override val coroutineContext: CoroutineContext,
   override val testCase: TestCase,
) : TerminalScope()
