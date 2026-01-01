package com.sksamuel.kotest.property

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.array
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.set

class DefaultCollectionSizeTest : DescribeSpec() {
   init {
      describe("PropertyTesting.defaultCollectionsRange configuration") {
         val old = PropertyTesting.defaultCollectionsRange
         PropertyTesting.defaultCollectionsRange = 52..52
         it("should be used by arrays") {
            Arb.array(Arb.constant(true)).next().shouldHaveSize(52)
         }
         it("should be used by lists") {
            Arb.list(Arb.constant(true)).next().shouldHaveSize(52)
         }
         it("should be used by sets") {
            Arb.set(Arb.int()).next().shouldHaveSize(52)
         }
         PropertyTesting.defaultCollectionsRange = old
      }
   }
}
