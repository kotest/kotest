package com.sksamuel.kotest.parallelism

import io.kotest.specs.StringSpec

class Test5 : StringSpec({
  "5" {
    Thread.sleep(2000)
  }
})