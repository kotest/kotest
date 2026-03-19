package io.kotest.plugin.intellij.locations

import io.kotest.matchers.shouldBe
import org.junit.Test

class EmbeddedLocationParserTest {

   @Test
   fun parseHappyPath() {
      EmbeddedLocationParser.parse("blah <kotest>lovely test</kotest>real name") shouldBe EmbeddedLocation(
         "lovely test",
         "real name"
      )
   }

   @Test
   fun parseInvalidString() {
      EmbeddedLocationParser.parse("blah <kost>lovely test</kotest>real name") shouldBe null
   }

   @Test
   fun parseRegularPath() {
      EmbeddedLocationParser.parse("real name") shouldBe null
   }
}
