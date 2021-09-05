package io.kotest.engine

import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.extensions.ProjectExtension
import io.kotest.engine.interceptors.EmptyTestSuiteInterceptor
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.interceptors.ProjectExtensionEngineInterceptor
import io.kotest.engine.interceptors.ProjectListenerEngineInterceptor
import io.kotest.engine.interceptors.ProjectTimeoutEngineInterceptor
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.engine.interceptors.SpecStyleValidationInterceptor
import io.kotest.engine.interceptors.TestDslStateInterceptor
import io.kotest.engine.interceptors.TestEngineListenerInitializeFinalizeInterceptor
import io.kotest.engine.interceptors.TestEngineListenerStartedFinishedInterceptor

actual fun testEngineInterceptors(conf: Configuration): List<EngineInterceptor> {
   return listOfNotNull(
      TestEngineListenerInitializeFinalizeInterceptor,
      ProjectTimeoutEngineInterceptor(conf.projectTimeout),
      TestDslStateInterceptor,
      SpecStyleValidationInterceptor,
      SpecSortEngineInterceptor,
      ProjectExtensionEngineInterceptor(configuration.extensions().filterIsInstance<ProjectExtension>()),
      ProjectListenerEngineInterceptor(configuration.extensions()),
      if (configuration.failOnEmptyTestSuite) EmptyTestSuiteInterceptor else null,
      TestEngineListenerStartedFinishedInterceptor,
   )
}
