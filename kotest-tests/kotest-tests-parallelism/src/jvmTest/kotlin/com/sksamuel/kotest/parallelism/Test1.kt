package com.sksamuel.kotest.parallelism

import io.kotest.specs.StringSpec

class Test1 : StringSpec({
  "1" {
    Thread.sleep(2000)
  }
})