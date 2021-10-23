package io.kotest.engine

import io.kotest.common.KotestInternal
import io.kotest.engine.interceptors.EmptyTestSuiteInterceptor
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.interceptors.ProjectExtensionEngineInterceptor
import io.kotest.engine.interceptors.ProjectListenerEngineInterceptor
import io.kotest.engine.interceptors.ProjectTimeoutEngineInterceptor
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.engine.interceptors.TestDslStateInterceptor
import io.kotest.engine.interceptors.TestEngineInitializedInterceptor
import io.kotest.engine.interceptors.TestEngineStartedFinishedInterceptor

@KotestInternal
internal actual fun testEngineInterceptors(): List<EngineInterceptor> {
   return listOfNotNull(
      TestEngineStartedFinishedInterceptor,
      ProjectTimeoutEngineInterceptor,
      TestDslStateInterceptor,
      SpecSortEngineInterceptor,
      ProjectExtensionEngineInterceptor,
      ProjectListenerEngineInterceptor,
      EmptyTestSuiteInterceptor,
      TestEngineInitializedInterceptor,
   )
}
