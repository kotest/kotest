import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.framework.teamcity.Escaper

class TeamCityEscaperTest : FunSpec() {
   init {
      test("escape pipe") {
         Escaper.escapeForTeamCity("qwe|qwe") shouldBe "qwe||qwe"
      }

      test("escape quote") {
         Escaper.escapeForTeamCity("qwe'qwe") shouldBe "qwe|'qwe"
      }

      test("escape new line") {
         Escaper.escapeForTeamCity("qwe\nqwe") shouldBe "qwe|nqwe"
      }

      test("escape carriage return") {
         Escaper.escapeForTeamCity("qwe\rqwe") shouldBe "qwe|rqwe"
      }

      test("escape open bracket") {
         Escaper.escapeForTeamCity("qwe[qwe") shouldBe "qwe|[qwe"
      }

      test("escape close bracket") {
         Escaper.escapeForTeamCity("qwe]qwe") shouldBe "qwe|]qwe"
      }

      test("escape next line") {
         Escaper.escapeForTeamCity("qwe\u0085qwe") shouldBe "qwe|xqwe"
      }

      test("escape paragraph") {
         Escaper.escapeForTeamCity("qwe\u2029qwe") shouldBe "qwe|pqwe"
      }

      test("escape line separator") {
         Escaper.escapeForTeamCity("qwe\u2028qwe") shouldBe "qwe|lqwe"
      }
   }
}
