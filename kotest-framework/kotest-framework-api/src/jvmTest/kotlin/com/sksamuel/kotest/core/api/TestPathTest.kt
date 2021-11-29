package com.sksamuel.kotest.core.api

import io.kotest.core.descriptors.TestPath
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestPathTest : FunSpec() {
   init {

      val spec = DescriptorTest::class.toDescriptor()
      val container = spec.append("a context")
      val test = container.append("nested test")

      test("test path should include spec and use spec separator") {
         container.path() shouldBe TestPath("com.sksamuel.kotest.core.api.DescriptorTest/a context")
      }

      test("test path should include parent tests and use test path separator") {
         test.path() shouldBe TestPath("com.sksamuel.kotest.core.api.DescriptorTest/a context -- nested test")
      }

      test("test path for spec should be fqn") {
         spec.path() shouldBe TestPath("com.sksamuel.kotest.core.api.DescriptorTest")
      }
   }
}
