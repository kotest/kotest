package com.sksamuel.kotest.engine.launcher

import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@Isolate
class PropertiesTest : DescribeSpec() {
   init {
      describe("kotest engine") {
         it("should apply kotest.properties") {
            System.getProperty("foo") shouldBe "boo"
         }
      }
   }
}
