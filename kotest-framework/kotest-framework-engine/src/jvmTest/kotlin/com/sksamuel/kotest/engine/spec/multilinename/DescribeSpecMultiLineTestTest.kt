package com.sksamuel.kotest.engine.spec.multilinename

import io.kotest.core.spec.style.DescribeSpec

class DescribeSpecMultiLineTestTest : DescribeSpec({
   describe("Multiline tagged test name") {
      it(
         """prevents test
            | from executing""".trimMargin()
      ).config {
      }
   }
})
