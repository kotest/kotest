package io.kotest.plugin.intellij.gradle

import io.kotest.matchers.shouldBe
import org.junit.Test

class VersionParserTest {

   @Test
   fun happyPath() {
      VersionParser.parse("6.1.0") shouldBe Version(6, 1, 0)
      VersionParser.parse("6.1.3") shouldBe Version(6, 1, 3)
      VersionParser.parse("12.34.45") shouldBe Version(12, 34, 45)
      VersionParser.parse("6.1.0-LOCAL") shouldBe Version(6, 1, 0)
      VersionParser.parse("6.1.0.123-SNAPSHOT") shouldBe Version(6, 1, 0)
   }

   @Test
   fun unhappyPath() {
      VersionParser.parse("6") shouldBe null
      VersionParser.parse("6.a") shouldBe null
      VersionParser.parse("6.1") shouldBe null
      VersionParser.parse("6a.12") shouldBe null
      VersionParser.parse("6.1b") shouldBe null
      VersionParser.parse("") shouldBe null
      VersionParser.parse("") shouldBe null
   }
}
