package com.sksamuel.kotest.engine.datatest

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.delay

class DataTestingApiTest : FunSpec() {

   private fun getNumbers() = arrayOf(1, 2, 3)

   init {

      context("oddness") {
         forAll(*getNumbers()) {
            delay(it.toLong())
            it % 2 shouldNotBe 0
         }
      }
   }
}
