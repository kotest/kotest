package com.sksamuel.kotest.runner.junit.platform

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.Segment
import io.kotest.runner.junit.platform.createUniqueIdForTest
import io.kotest.runner.junit.platform.createUniqueIdForSpec
import org.junit.platform.engine.UniqueId

class UniqueIdTest : FunSpec() {
   init {

      test("createUniqueIdForSpec") {
         val root = UniqueId.forEngine("kotest")
         createUniqueIdForSpec(root, UniqueIdTest::class.toDescriptor().id) shouldBe
            root.append(Segment.Spec.value, "com.sksamuel.kotest.runner.junit.platform.UniqueIdTest")
      }

      test("createTestUniqueId should handle rooot tests") {
         val root = UniqueId.forEngine("kotest")
         val testDescriptor = UniqueIdTest::class.toDescriptor().append("a")
         createUniqueIdForTest(root, testDescriptor) shouldBe
            root.append(Segment.Spec.value, "com.sksamuel.kotest.runner.junit.platform.UniqueIdTest")
               .append(Segment.Test.value, "a")
      }

      test("createTestUniqueId should handle nested tests") {
         val root = UniqueId.forEngine("kotest")
         val testDescriptor = UniqueIdTest::class.toDescriptor().append("a").append("b")
         createUniqueIdForTest(root, testDescriptor) shouldBe
            root.append(Segment.Spec.value, "com.sksamuel.kotest.runner.junit.platform.UniqueIdTest")
               .append(Segment.Test.value, "a")
               .append(Segment.Test.value, "b")
      }
   }
}
