package com.sksamuel.kotlintest.parallelism

import io.kotlintest.specs.StringSpec

class Test1 : StringSpec({
  "1" {
    Thread.sleep(2000)
  }
})