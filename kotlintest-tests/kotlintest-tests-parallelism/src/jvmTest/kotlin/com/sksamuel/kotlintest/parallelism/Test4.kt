package com.sksamuel.kotlintest.parallelism

import io.kotlintest.specs.StringSpec

class Test4 : StringSpec({
  "4" {
    Thread.sleep(2000)
  }
})