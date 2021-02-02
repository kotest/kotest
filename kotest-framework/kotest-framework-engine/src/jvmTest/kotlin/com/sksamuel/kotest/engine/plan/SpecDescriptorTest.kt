package com.sksamuel.kotest.engine.plan

import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DisplayName("GGGGGGGGG")
class SpecDescriptorTest : FunSpec() {
   init {
      test("@DisplayName should be used when generating a spec name") {
         Descriptor.fromSpecClass(SpecDescriptorTest::class).displayName.value shouldBe "GGGGGGGGG"
      }
      test("classname should be set when generating a spec name") {
         Descriptor.fromSpecClass(SpecDescriptorTest::class).classname shouldBe "com.sksamuel.kotest.engine.plan.SpecDescriptorTest"
      }
      test("name should be set when generating a spec name") {
         Descriptor.fromSpecClass(SpecDescriptorTest::class).name.value shouldBe "com.sksamuel.kotest.engine.plan.SpecDescriptorTest"
      }
      test("test path should be set when generating a spec name") {
         Descriptor.fromSpecClass(SpecDescriptorTest::class).testPath().value shouldBe "kotest/com.sksamuel.kotest.engine.plan.SpecDescriptorTest"
      }
   }
}
