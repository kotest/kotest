package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.property.checkAll
import io.kotest.property.forAll

class NullableTest : FunSpec({
   test("forAll with implicit nullable arbitaries") {
      val iterations = 1000
      val classifications = forAll<Int?>(iterations) { num ->
         classify(num == null, "null", "non-null")
         true
      }.classifications()
      classifications["null"]?.shouldBeBetween(300, 600)
      classifications["non-null"]?.shouldBeBetween(300, 600)
   }

   test("checkAll with implicit nullable arbitaries") {
      val iterations = 1000
      val classifications = checkAll<Int?>(iterations) { num ->
         classify(num == null, "null", "non-null")
      }.classifications()
      classifications["null"]?.shouldBeBetween(300, 600)
      classifications["non-null"]?.shouldBeBetween(300, 600)
   }
})
