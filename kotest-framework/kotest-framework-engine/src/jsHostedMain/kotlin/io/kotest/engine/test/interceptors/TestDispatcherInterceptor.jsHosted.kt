package io.kotest.engine.test.interceptors

internal actual fun testDispatcherInterceptor(): TestExecutionInterceptor {
  return TestExecutionInterceptor.Noop
}
