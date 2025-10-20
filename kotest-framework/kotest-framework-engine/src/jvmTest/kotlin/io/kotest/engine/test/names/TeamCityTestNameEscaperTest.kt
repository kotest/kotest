package io.kotest.engine.test.names

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.teamcity.names.TeamCityTestNameEscaper
import io.kotest.matchers.shouldBe

class TeamCityTestNameEscaperTest : FunSpec() {
   init {
      test("should escape periods") {
         TeamCityTestNameEscaper.escape("foo.bar") shouldBe "foo bar"
      }
   }
}
