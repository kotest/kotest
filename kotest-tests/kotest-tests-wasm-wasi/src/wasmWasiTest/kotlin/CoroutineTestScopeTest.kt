package com.sksamuel.kotest.wasm

import io.kotest.core.spec.style.FunSpec

class CoroutineTestScopeTest : FunSpec({

   context("nested coroutineTestScope should not hang on wasm-wasi").config(coroutineTestScope = true) {
      test("foo") {}
   }

   test("root coroutineTestScope should not hang on wasm-wasi").config(coroutineTestScope = true) {
   }

   test("a") {
   }


})
