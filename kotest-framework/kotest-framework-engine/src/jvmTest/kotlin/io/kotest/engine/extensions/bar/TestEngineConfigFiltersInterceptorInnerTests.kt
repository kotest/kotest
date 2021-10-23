package io.kotest.engine.extensions.bar

import io.kotest.core.spec.style.FunSpec
import com.sksamuel.kotest.engine.interceptors.testAndIncrementCounter

class TestEngineConfigFiltersInterceptorInnerTests : FunSpec({
   test("bar test a") { testAndIncrementCounter() }
   test("bar test b") { testAndIncrementCounter() }
})
