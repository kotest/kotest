package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.config.ExperimentalKotest
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.Description
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.config.deriveTestCaseConfig
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.config.deriveTestContainerConfig
import io.kotest.core.test.toTestConfig
import io.kotest.core.test.toTestContainerConfig
import kotlin.time.Duration

class RootTestWithConfigBuilder(
   private val name: DescriptionName.TestName,
   private val registration: RootTestRegistration,
   private val xdisabled: Boolean
) {

   fun config(
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
      val derivedConfig = registration.defaultConfig.deriveTestCaseConfig(
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
      registration.addTest(name, xdisabled, derivedConfig, TestType.Test, test)
   }
}

@ExperimentalKotest
class FunSpecRootContainerBuilder(
   private val name: DescriptionName.TestName,
   private val description: Description,
   private val registration: RootTestRegistration,
   private val lifecycle: Lifecycle,
   private val xdisabled: Boolean
) {

   @ExperimentalKotest
   fun config(
      enabled: Boolean? = null,
      enabledIf: EnabledIf? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      test: suspend FunSpecContextScope.() -> Unit
   ) {
      val derivedConfig = registration.defaultConfig.toTestContainerConfig().deriveTestContainerConfig(
         enabled,
         enabledIf,
         tags,
         timeout,
      )
      registration.addTest(
         name,
         xdisabled,
         derivedConfig.toTestConfig(),
         TestType.Container
      ) {
         FunSpecContextScope(
            description,
            lifecycle,
            this,
            registration.defaultConfig,
            coroutineContext
         ).test()
      }
   }
}

@ExperimentalKotest
class ShouldSpecRootContainerBuilder(
   private val name: DescriptionName.TestName,
   private val description: Description,
   private val registration: RootTestRegistration,
   private val lifecycle: Lifecycle,
   private val xdisabled: Boolean
) {

   @ExperimentalKotest
   fun config(
      enabled: Boolean? = null,
      enabledIf: EnabledIf? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      test: suspend ShouldSpecContextScope.() -> Unit
   ) {
      val derivedConfig = registration.defaultConfig.toTestContainerConfig().deriveTestContainerConfig(
         enabled,
         enabledIf,
         tags,
         timeout,
      )
      registration.addTest(
         name,
         xdisabled,
         derivedConfig.toTestConfig(),
         TestType.Container
      ) {
         ShouldSpecContextScope(
            description,
            lifecycle,
            this,
            registration.defaultConfig,
            coroutineContext
         ).test()
      }
   }
}

