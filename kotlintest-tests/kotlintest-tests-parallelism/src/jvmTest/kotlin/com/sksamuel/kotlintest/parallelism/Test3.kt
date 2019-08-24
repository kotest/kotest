package com.sksamuel.kotlintest.parallelism

import io.kotlintest.specs.StringSpec

class Test3 : StringSpec({
  "3" {
    Thread.sleep(2000)
  }
})