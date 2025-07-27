package com.sksamuel.kotest.engine.active

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.descriptors.toDescriptor
import io.kotest.matchers.shouldBe

class FocusBangTest : FreeSpec() {
   init {
      "test case with f: prefix should be focused" {
         val test = TestCase(
            name = TestNameBuilder.builder("f: a").build(),
            descriptor = FocusBangTest::class.toDescriptor().append("f: a"),
            spec = this@FocusBangTest,
            test = {},
            type = TestType.Test,
         )
         test.name.focus shouldBe true
      }

      "test case with no prefix should not be focused" {
         val test = TestCase(
            name = TestNameBuilder.builder("a").build(),
            descriptor = FocusBangTest::class.toDescriptor().append("a"),
            spec = this@FocusBangTest,
            test = {},
            type = TestType.Test,
         )
         test.name.focus shouldBe false
      }
   }
}
