package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.descriptors.append
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestName
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import io.kotest.core.test.deriveTestCaseConfig
import kotlin.time.Duration

@Deprecated("Renamed to FreeSpecContainerContext. Deprecated since 4.5.")
typealias FreeScope = FreeSpecContainerContext

class FreeSpecContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> name.minus(test)
         TestType.Test -> name.invoke(test)
      }
   }

   /**
    * Creates a new container scope inside this spec.
    */
   suspend infix operator fun String.minus(test: suspend FreeSpecContainerContext.() -> Unit) {
      registerTestCase(createNestedTest(this, TestType.Container) {
         val incomplete = IncompleteContainerContext(this)
         FreeSpecContainerContext(incomplete).test()
         if (!incomplete.hasNestedTest) throw IncompleteContainerException(this@minus)
      })
   }

   /**
    * Creates a new terminal test scope inside this spec.
    */
   suspend infix operator fun String.invoke(test: suspend FreeSpecTerminalContext.() -> Unit) {
      registerTestCase(createNestedTest(this, TestType.Test) { FreeSpecTerminalContext(this).test() })
   }

   private fun createNestedTest(name: String, type: TestType, test: suspend TestContext.() -> Unit): NestedTest {
      return createNestedTest(
         descriptor = testCase.descriptor.append(name),
         name = TestName(name),
         xdisabled = false,
         config = testCase.spec.resolvedDefaultConfig(),
         type = type,
         factoryId = testCase.factoryId,
         test = test
      )
   }

   suspend fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      test: suspend TestContext.() -> Unit,
   ) = TestWithConfigBuilder(
      TestName(this),
      testContext,
      testCase.spec.resolvedDefaultConfig(),
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
      severity,
      test
   )

   // eg, "this test".config(...) - { } // adds a container test with config
   suspend infix operator fun FreeSpecContextConfigBuilder.minus(test: suspend FreeSpecContainerContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            descriptor = testCase.descriptor.append(name),
            name = TestName(name),
            xdisabled = false,
            config = config,
            type = TestType.Container,
            factoryId = testCase.factoryId,
         ) {
            val incomplete = IncompleteContainerContext(this)
            FreeSpecContainerContext(incomplete).test()
            if (!incomplete.hasNestedTest) throw IncompleteContainerException(name)
         })
   }

   // starts a config builder for a context with config
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
   ): FreeSpecContextConfigBuilder {
      val config = testContext.testCase.config.deriveTestCaseConfig(
         enabled = enabled,
         tags = tags,
         extensions = extensions,
         timeout = timeout,
         invocationTimeout = invocationTimeout,
         enabledIf = enabledIf,
         invocations = invocations,
         threads = threads,
         severity = severity,
         failfast = failfast,
      )
      return FreeSpecContextConfigBuilder(this, config)
   }
}
