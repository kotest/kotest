package com.sksamuel.kotest.engine.plan

import io.kotest.core.plan.NodeName
import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DisplayName("ZZZZZ")
class SpecNameTest : FunSpec() {
   init {
      test("@DisplayName should be used when generating a spec name") {
         NodeName.fromSpecClass(SpecNameTest::class).displayName shouldBe "ZZZZZ"
      }
      test("fqn should be set when generating a spec name") {
         NodeName.fromSpecClass(SpecNameTest::class).fqn shouldBe "com.sksamuel.kotest.engine.plan.SpecNameTest"
      }
      test("name should be set when generating a spec name") {
         NodeName.fromSpecClass(SpecNameTest::class).name shouldBe "com.sksamuel.kotest.engine.plan.SpecNameTest"
      }
      test("full name should be set when generating a spec name") {
         NodeName.fromSpecClass(SpecNameTest::class).fullName shouldBe "kotest/com.sksamuel.kotest.engine.plan.SpecNameTest"
      }
   }
}
