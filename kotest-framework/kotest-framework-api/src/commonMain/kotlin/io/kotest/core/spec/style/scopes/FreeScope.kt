package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.Description
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createTestName
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class FreeScope(
   override val description: Description,
   override val lifecycle: Lifecycle,
   override val testContext: TestContext,
   override val defaultConfig: TestCaseConfig,
   override val coroutineContext: CoroutineContext,
) : ContainerScope {

   suspend infix operator fun String.minus(test: suspend FreeScope.() -> Unit) {
      val name = createTestName(this)
      addContainerTest(name, xdisabled = false) {
         FreeScope(
            this@FreeScope.description.append(name, TestType.Container),
            this@FreeScope.lifecycle,
            this,
            this@FreeScope.defaultConfig,
            this@FreeScope.coroutineContext,
         ).test()
      }
   }

   suspend infix operator fun String.invoke(test: suspend TestContext.() -> Unit) =
      addTest(createTestName(this), xdisabled = false, test = test)

   @OptIn(ExperimentalTime::class)
   suspend fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      test: suspend TestContext.() -> Unit
   ) = TestWithConfigBuilder(
      createTestName(this),
      testContext,
      defaultConfig,
      xdisabled = false,
   ).config(
      enabled,
      invocations,
      threads,
      tags,
      timeout,
      extensions,
      enabledIf,
      invocationTimeout,
      test
   )
}
