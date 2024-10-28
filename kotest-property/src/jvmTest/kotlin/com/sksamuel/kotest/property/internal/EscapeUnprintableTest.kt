package com.sksamuel.kotest.property.internal

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.internal.escapeUnprintable

class EscapeUnprintableTest : StringSpec({

   "should leave printable characters unchanged" {
      val input = "Hello, World!"
      val result = input.escapeUnprintable()
      result shouldBe "Hello, World!"
   }

   "should escape a single control character" {
      val input = "\u0007"
      val result = input.escapeUnprintable()
      result shouldBe "U+0007"
   }

   "should escape multiple control characters" {
      val input = "\u0007\u0008"
      val result = input.escapeUnprintable()
      result shouldBe "U+0007U+0008"
   }

   "should escape control characters mixed with printable characters" {
      val input = "Hello\u0007World"
      val result = input.escapeUnprintable()
      result shouldBe "HelloU+0007World"
   }

   "should handle empty string" {
      val input = ""
      val result = input.escapeUnprintable()
      result shouldBe ""
   }

   "should escape multiple control characters at various positions" {
      val input = "\u0007Hello\u0008World\u001B"
      val result = input.escapeUnprintable()
      result shouldBe "U+0007HelloU+0008WorldU+001B"
   }

   "should escape non-ASCII control characters" {
      val input = "\u009F"
      val result = input.escapeUnprintable()
      result shouldBe "U+009F"
   }
})
