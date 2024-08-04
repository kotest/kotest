package io.kotest.engine

import io.kotest.common.KotestInternal
import io.kotest.engine.interceptors.DumpConfigInterceptor
import io.kotest.engine.interceptors.EmptyTestSuiteInterceptor
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.interceptors.MarkAbortedExceptionsAsSkippedTestInterceptor
import io.kotest.engine.interceptors.ProjectExtensionEngineInterceptor
import io.kotest.engine.interceptors.ProjectListenerEngineInterceptor
import io.kotest.engine.interceptors.ProjectTimeoutEngineInterceptor
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.engine.interceptors.TestDslStateInterceptor
import io.kotest.engine.interceptors.TestEngineInitializedInterceptor
import io.kotest.engine.interceptors.TestEngineStartedFinishedInterceptor
import io.kotest.engine.interceptors.WriteFailuresInterceptor
import io.kotest.engine.test.interceptors.TestExecutionInterceptor

@KotestInternal
actual fun testEngineInterceptors(): List<EngineInterceptor> {
   return listOfNotNull(
      TestEngineStartedFinishedInterceptor,
      TestDslStateInterceptor,
      SpecSortEngineInterceptor,
      ProjectExtensionEngineInterceptor,
      ProjectListenerEngineInterceptor,
      ProjectTimeoutEngineInterceptor,
      EmptyTestSuiteInterceptor,
      WriteFailuresInterceptor,
      DumpConfigInterceptor,
      TestEngineInitializedInterceptor,
   )
}

@KotestInternal
internal actual fun testInterceptorsForPlatform(): List<TestExecutionInterceptor> =
   listOf(MarkAbortedExceptionsAsSkippedTestInterceptor)
