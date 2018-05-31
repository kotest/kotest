package com.sksamuel.kotlintest.parallelism

import io.kotlintest.specs.StringSpec

class Test2 : StringSpec({
  "2" {
    Thread.sleep(2000)
  }
})