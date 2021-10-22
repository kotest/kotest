package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.descriptors.append
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import io.kotest.core.test.deriveTestCaseConfig
import kotlin.time.Duration

class TestWithConfigBuilder(
   private val name: TestName,
   private val context: TestContext,
   private val defaultTestConfig: TestCaseConfig,
   private val xdisabled: Boolean,
) {

   suspend fun config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      test: suspend TestContext.() -> Unit
   ) {

      TestDslState.clear(context.testCase.descriptor.append(name))

      val derivedConfig = defaultTestConfig.deriveTestCaseConfig(
         enabled,
         tags,
         extensions,
         timeout,
         invocationTimeout,
         enabledIf,
         invocations,
         threads,
         severity
      )
      context.registerTestCase(
         createNestedTest(
            descriptor = context.testCase.descriptor.append(name),
            name = name,
            xdisabled = xdisabled,
            config = derivedConfig,
            type = TestType.Test,
            factoryId = null,
            test = test,
         )
      )
   }
}
