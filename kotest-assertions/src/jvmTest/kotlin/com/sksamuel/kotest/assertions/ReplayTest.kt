package com.sksamuel.kotest.assertions

import io.kotest.assertions.replay
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize

class ReplayTest : FunSpec({

   test("multiple threads") {
      val threads = mutableSetOf<Long>()
      replay(10, 3) {
         threads.add(Thread.currentThread().id)
      }
      threads.shouldHaveSize(3)
   }

   test("listener") {
      val befores = mutableSetOf<Int>()
      val afters = mutableSetOf<Int>()
      replay(10, 3, before = { befores.add(it) }, after = { afters.add(it) }) {

      }
      befores.shouldHaveSize(10)
      afters.shouldHaveSize(10)
   }

})
