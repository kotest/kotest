package com.sksamuel.kotest.parallelism

import io.kotest.core.spec.style.StringSpec

class Test3 : StringSpec({
   "3" {
      Thread.sleep(500)
   }
})
