package io.kotest.engine.extensions.foo

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.testAndIncrementCounter

class TestEngineConfigFiltersInterceptorInnerTests : FunSpec({
   test("foo test a") { testAndIncrementCounter() }
   test("foo test b") { testAndIncrementCounter() }
})
