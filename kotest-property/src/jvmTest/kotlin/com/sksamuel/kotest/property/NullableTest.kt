package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.property.checkAll
import io.kotest.property.forAll
import io.kotest.property.forNone
import io.kotest.property.withAssumptions

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

   test("forNone with implicit nullable arbitraries") {
      val iterations = 1000
      val classifications = forNone<Int?>(iterations) { num ->
         classify(num == null, "null", "non-null")
         false
      }.classifications()
      classifications["null"]?.shouldBeBetween(300, 600)
      classifications["non-null"]?.shouldBeBetween(300, 600)
   }

   test("checkAll with implicit nullable arbitraries") {
      val iterations = 1000
      val classifications = checkAll<Int?>(iterations) { num ->
         classify(num == null, "null", "non-null")
      }.classifications()
      classifications["null"]?.shouldBeBetween(300, 600)
      classifications["non-null"]?.shouldBeBetween(300, 600)
   }

   test("checkAll with implicit nullable arbitraries with should not be null Assumption") {
      checkAll<Int?> { num ->
         withAssumptions(num != null) {
            num != null
         }
      }
   }

   test("checkAll with implicit nullable arbitraries with should be null Assumption") {
      checkAll<Int?> { num ->
         withAssumptions(num == null) {
            num == null
         }
      }
   }
})
