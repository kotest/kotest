package io.kotest.engine

import io.kotest.core.config.configuration
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.Spec
import io.kotest.engine.interceptors.DumpConfigInterceptor
import io.kotest.engine.interceptors.EmptyTestSuiteInterceptor
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.interceptors.KotestPropertiesInterceptor
import io.kotest.engine.interceptors.ProjectExtensionEngineInterceptor
import io.kotest.engine.interceptors.ProjectListenerEngineInterceptor
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.engine.interceptors.TestDslStateInterceptor
import io.kotest.engine.interceptors.WriteFailuresInterceptor

actual class SpecRunner {
   /**
    * Execute the given [spec] and invoke the [onComplete] callback once finished.
    */
   actual fun execute(spec: Spec, onComplete: suspend () -> Unit) {
   }
}

actual fun testEngineInterceptors(): List<EngineInterceptor> {
   return listOfNotNull(
      KotestPropertiesInterceptor,
      TestDslStateInterceptor,
      SpecSortEngineInterceptor,
      ProjectExtensionEngineInterceptor(configuration.extensions().filterIsInstance<ProjectExtension>()),
      ProjectListenerEngineInterceptor(configuration.extensions()),
      WriteFailuresInterceptor(configuration.specFailureFilePath),
      if (System.getProperty(KotestEngineProperties.dumpConfig) == null) null else DumpConfigInterceptor(configuration),
      if (configuration.failOnEmptyTestSuite) EmptyTestSuiteInterceptor else null,
   )
}
