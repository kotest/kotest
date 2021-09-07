package io.kotest.engine

import io.kotest.core.config.Configuration
import io.kotest.core.extensions.ProjectExtension
import io.kotest.engine.interceptors.EmptyTestSuiteInterceptor
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.interceptors.ProjectExtensionEngineInterceptor
import io.kotest.engine.interceptors.ProjectListenerEngineInterceptor
import io.kotest.engine.interceptors.ProjectTimeoutEngineInterceptor
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.engine.interceptors.TestDslStateInterceptor
import io.kotest.engine.interceptors.TestEngineListenerStartedFinishedInterceptor
import io.kotest.engine.interceptors.TestEngineStartupShutdownInterceptor

actual fun testEngineInterceptors(conf: Configuration): List<EngineInterceptor> {
   return listOfNotNull(
      TestEngineStartupShutdownInterceptor,
      ProjectTimeoutEngineInterceptor(conf.projectTimeout),
      TestDslStateInterceptor,
      SpecSortEngineInterceptor,
      ProjectExtensionEngineInterceptor(conf.extensions().filterIsInstance<ProjectExtension>()),
      ProjectListenerEngineInterceptor(conf.extensions()),
      if (conf.failOnEmptyTestSuite) EmptyTestSuiteInterceptor else null,
      TestEngineListenerStartedFinishedInterceptor,
   )
}
