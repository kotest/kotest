package io.kotest.engine.extensions.foo

import io.kotest.core.spec.style.FunSpec
import com.sksamuel.kotest.engine.interceptors.testAndIncrementCounter

class TestEngineConfigFiltersInterceptorInnerTests : FunSpec({
   test("foo test a") { testAndIncrementCounter() }
   test("foo test b") { testAndIncrementCounter() }
})
