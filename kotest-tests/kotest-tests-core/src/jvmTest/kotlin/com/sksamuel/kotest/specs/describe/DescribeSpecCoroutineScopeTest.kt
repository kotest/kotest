package com.sksamuel.kotest.specs.describe

import io.kotest.core.spec.style.DescribeSpec
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DescribeSpecCoroutineScopeTest : DescribeSpec() {

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
