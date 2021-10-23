package io.kotest.engine.extensions.bar

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.testAndIncrementCounter

class TestEngineConfigFiltersInterceptorInnerTests : FunSpec({
   test("bar test a") { testAndIncrementCounter() }
   test("bar test b") { testAndIncrementCounter() }
})
