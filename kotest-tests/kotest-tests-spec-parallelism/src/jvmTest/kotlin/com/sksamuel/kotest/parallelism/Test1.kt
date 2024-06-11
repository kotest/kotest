package com.sksamuel.kotest.parallelism

import io.kotest.core.spec.style.StringSpec

class Test1 : StringSpec({
   "a" {
      Thread.sleep(500)
   }
})
