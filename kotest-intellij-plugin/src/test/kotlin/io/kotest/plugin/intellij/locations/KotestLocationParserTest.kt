package io.kotest.plugin.intellij.locations

import io.kotest.matchers.shouldBe
import org.junit.Test

class KotestLocationParserTest {

   @Test
   fun parseHappyPath() {
      KotestLocationParser.parse("blah <kotest>lovely test</kotest>real name") shouldBe KotestLocation(
         "lovely test",
         "real name"
      )
   }

   @Test
   fun parseInvalidString() {
      KotestLocationParser.parse("blah <kost>lovely test</kotest>real name") shouldBe null
   }

   @Test
   fun parseRegularPath() {
      KotestLocationParser.parse("real name") shouldBe null
   }
}
