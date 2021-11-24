package com.sksamuel.kotest.engine.spec.coroutine

import io.kotest.core.spec.style.DescribeSpec
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DescribeSpecCoroutineTest : DescribeSpec() {

   init {
      describe("a") {
         it("b") {
            launch {
               delay(100)
            }
         }
      }
   }
}
