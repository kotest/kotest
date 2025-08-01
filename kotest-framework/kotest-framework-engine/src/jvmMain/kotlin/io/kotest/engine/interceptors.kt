package io.kotest.engine

import io.kotest.engine.interceptors.DumpProjectConfigInterceptor
import io.kotest.engine.interceptors.EmptyTestSuiteInterceptor
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.interceptors.MarkPlatformAbortedExceptionsAsSkippedTestInterceptor
import io.kotest.engine.interceptors.ProjectConfigurationEngineInterceptor
import io.kotest.engine.interceptors.ProjectExtensionEngineInterceptor
import io.kotest.engine.interceptors.ProjectListenerEngineInterceptor
import io.kotest.engine.interceptors.ProjectTimeoutEngineInterceptor
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.engine.interceptors.SystemPropertiesEngineInterceptor
import io.kotest.engine.interceptors.TestDslStateInterceptor
import io.kotest.engine.interceptors.TestEngineInitializedInterceptor
import io.kotest.engine.interceptors.TestEngineStartedFinishedInterceptor
import io.kotest.engine.interceptors.WriteFailuresInterceptor
import io.kotest.engine.test.interceptors.TestExecutionInterceptor

actual fun testEngineInterceptorsForPlatform(): List<EngineInterceptor> {
   return listOfNotNull(
      // this must be first to allow other interceptors to be configured by system properties
      SystemPropertiesEngineInterceptor,
      // this must come after system properties because the system properties can be used to configure the project config location
      ProjectConfigurationEngineInterceptor,
      TestEngineStartedFinishedInterceptor,
      TestDslStateInterceptor,
      SpecSortEngineInterceptor,
      ProjectExtensionEngineInterceptor,
      ProjectListenerEngineInterceptor,
      ProjectTimeoutEngineInterceptor,
      EmptyTestSuiteInterceptor,
      WriteFailuresInterceptor,
      DumpProjectConfigInterceptor,
      TestEngineInitializedInterceptor,
   )
}

internal actual fun testInterceptorsForPlatform(): List<TestExecutionInterceptor> =
   listOf(MarkPlatformAbortedExceptionsAsSkippedTestInterceptor)
