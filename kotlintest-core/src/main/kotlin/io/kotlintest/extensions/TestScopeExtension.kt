package io.kotlintest.extensions

import io.kotlintest.TestScope

interface TestScopeExtension {
  fun intercept(scope: TestScope, test: () -> Unit)
}