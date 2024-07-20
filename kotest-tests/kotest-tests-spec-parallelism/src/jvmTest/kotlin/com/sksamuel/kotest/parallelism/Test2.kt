package com.sksamuel.kotest.parallelism

import io.kotest.core.spec.style.StringSpec

class Test2 : StringSpec({
   "2" {
      Thread.sleep(500)
   }
})
