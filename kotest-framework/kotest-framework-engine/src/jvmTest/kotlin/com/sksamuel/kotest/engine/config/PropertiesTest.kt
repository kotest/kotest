package com.sksamuel.kotest.engine.config

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@Isolate
class PropertiesTest : DescribeSpec() {
   init {
      describe("kotest engine") {
         it("should apply kotest.properties") {
            System.getProperty("tears") shouldBe "fears"
         }
      }
   }
}
