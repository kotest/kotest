package com.sksamuel.kotest.engine.interceptors.filters2

import com.sksamuel.kotest.engine.interceptors.testAndIncrementCounter
import io.kotest.core.spec.style.FunSpec

class FooTests : FunSpec({
   test("foo test a") { testAndIncrementCounter() }
   test("foo test b") { testAndIncrementCounter() }
})
