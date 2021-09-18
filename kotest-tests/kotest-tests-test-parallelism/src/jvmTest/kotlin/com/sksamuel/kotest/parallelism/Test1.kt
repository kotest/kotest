package com.sksamuel.kotest.parallelism

import io.kotest.core.spec.style.FunSpec

class Test1 : FunSpec({
   repeat(10) { k ->
      test("$k") {
         Thread.sleep(100)
      }
   }
})
