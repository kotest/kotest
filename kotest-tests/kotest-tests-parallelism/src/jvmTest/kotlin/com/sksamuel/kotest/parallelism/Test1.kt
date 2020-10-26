package com.sksamuel.kotest.parallelism

import io.kotest.core.spec.style.StringSpec

class Test1 : StringSpec({
   "1" {
      Thread.sleep(2000)
   }
})
