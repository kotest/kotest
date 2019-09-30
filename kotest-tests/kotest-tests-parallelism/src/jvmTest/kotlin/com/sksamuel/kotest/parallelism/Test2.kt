package com.sksamuel.kotest.parallelism

import io.kotest.specs.StringSpec

class Test2 : StringSpec({
  "2" {
    Thread.sleep(2000)
  }
})