package io.kotest.matchers.string

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MapFileEndingsToUnixStringPreprocessorTest : FunSpec() {
   init {
      test("should map windows") {
         val mixedString = "Windows\r\nUnix\nOldMac\r"
         val unixString = "Windows\nUnix\nOldMac\n"
         MapFileEndingsToUnixStringPreprocessor.map(mixedString) shouldBe unixString
      }
   }
}
