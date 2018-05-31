package com.sksamuel.kotlintest.parallelism

import io.kotlintest.specs.StringSpec

class Test5 : StringSpec({
  "5" {
    Thread.sleep(2000)
  }
})