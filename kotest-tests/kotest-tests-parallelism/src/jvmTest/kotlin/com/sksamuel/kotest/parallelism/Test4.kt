package com.sksamuel.kotest.parallelism

import io.kotest.core.spec.style.StringSpec

class Test4 : StringSpec({
   "4" {
      Thread.sleep(2000)
   }
})
