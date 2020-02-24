package com.sksamuel.kotest.autoscan

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AutoScanConstructorSpec(private val a: String, private val b: String) : FunSpec({
   test("foo") {
      a.shouldBe(b)
   }
})
