package io.kotlintest.extensions

import io.kotlintest.TestScope

interface TestScopeInterceptor {
  fun intercept(scope: TestScope, test: () -> Unit)
}