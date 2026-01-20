package io.kotest.engine.test.interceptors

internal actual fun testInterceptorsForPlatform(): List<TestExecutionInterceptor> {
   return listOf(MarkOpenTest4jAbortedExceptionsAsSkippedTestInterceptor)
}
