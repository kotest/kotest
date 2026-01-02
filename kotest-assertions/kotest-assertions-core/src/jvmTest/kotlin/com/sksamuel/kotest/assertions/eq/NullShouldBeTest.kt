package com.sksamuel.kotest.assertions.eq

import io.kotest.core.annotation.Issue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.be
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe

class MyStatefulObject : Iterable<Int> {
   var state: Int? = 5
   override fun iterator(): Iterator<Int> = iterator {
      // The goal is to make the iterator return just one `5` and then get exhausted.
      if (state != null) {
         yield(state!!)
         state = null
      }
   }
}

@Issue("https://github.com/kotest/kotest/issues/5036")
class NullShouldBeTest : FunSpec({
   test("shouldBe null and shouldNotBe null should handle nulls without altering state") {
      val obj = MyStatefulObject()

      obj shouldNotBe null
      obj shouldNot be(null)
      obj.shouldNotBeNull()
      obj shouldNot beNull()

      // the iterable should not have been consumed in the previous assertions
      val iterable = obj as Iterable<*>
      iterable.iterator().hasNext() shouldBe true
   }
})
