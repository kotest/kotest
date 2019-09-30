package com.sksamuel.kotest.parallelism

import io.kotest.specs.StringSpec

class Test3 : StringSpec({
  "3" {
    Thread.sleep(2000)
  }
})