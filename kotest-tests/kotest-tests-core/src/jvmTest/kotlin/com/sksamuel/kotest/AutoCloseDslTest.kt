package com.sksamuel.kotest

import io.kotest.core.spec.style.FunSpec

class AutoCloseDslTest : FunSpec({

   val closeme = AutoCloseable {

   }

   autoClose(closeme)

   test("auto close with dsl method") {

   }
})
