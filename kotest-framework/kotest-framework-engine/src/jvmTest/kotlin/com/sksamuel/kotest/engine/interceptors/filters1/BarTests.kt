package com.sksamuel.kotest.engine.interceptors.filters1

import io.kotest.core.spec.style.FunSpec
import com.sksamuel.kotest.engine.interceptors.testAndIncrementCounter

class BarTests : FunSpec({
   test("bar test a") { testAndIncrementCounter() }
   test("bar test b") { testAndIncrementCounter() }
})

