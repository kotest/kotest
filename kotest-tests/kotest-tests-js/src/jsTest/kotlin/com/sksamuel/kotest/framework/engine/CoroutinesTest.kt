package com.sksamuel.kotest.framework.engine

import io.kotest.core.spec.style.DescribeSpec
import kotlinx.coroutines.delay

class CoroutinesTest : DescribeSpec() {
   init {
      describe("context") {
         delay(125) // coroutines all levels
         it("should work") {
            delay(125) // coroutines all levels
         }
      }
   }
}
