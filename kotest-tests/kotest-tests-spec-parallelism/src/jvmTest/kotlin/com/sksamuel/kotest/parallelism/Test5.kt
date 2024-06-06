package com.sksamuel.kotest.parallelism

import io.kotest.core.spec.style.StringSpec

class Test5 : StringSpec({
   "5" {
      Thread.sleep(500)
   }
})
