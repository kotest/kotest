package com.sksamuel.kotest.engine.tags

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.tags.Expression
import io.kotest.engine.tags.Parser
import io.kotest.engine.tags.expression
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class TagParserTest : FunSpec() {
   init {

      test("ident") {
         Parser.from("mytag").expression() shouldBe Expression.Identifier("mytag")
      }

      test("or") {
         Parser.from("mytag | othertag").expression() shouldBe Expression.Or(
            Expression.Identifier("mytag"),
            Expression.Identifier("othertag")
         )
      }

      test("and") {
         Parser.from("mytag & othertag").expression() shouldBe Expression.And(
            Expression.Identifier("mytag"),
            Expression.Identifier("othertag")
         )
      }

      test("not") {
         Parser.from("!mytag").expression() shouldBe Expression.Not(Expression.Identifier("mytag"))
      }

      // & binds tighter than |, so `a & b | c` parses as Or(And(a, b), c)
      test("and binds tighter than or") {
         Parser.from("a & b | c").expression() shouldBe Expression.Or(
            Expression.And(
               Expression.Identifier("a"),
               Expression.Identifier("b")
            ),
            Expression.Identifier("c")
         )
      }

      test("advanced") {
         Parser.from("(mytag & !othertag | thistag) & thattag").expression() shouldBe Expression.And(
            Expression.Or(
               Expression.And(
                  Expression.Identifier("mytag"),
                  Expression.Not(Expression.Identifier("othertag"))
               ),
               Expression.Identifier("thistag")
            ),
            Expression.Identifier("thattag")
         )
      }

      test("odd characters") {
         Parser.from("(my#%#e123!#!@TAG & !____9123231.... | '''',.''/'l/'l/''/'''') & thattag")
            .expression() shouldBe Expression.And(
            Expression.Or(
               Expression.And(
                  Expression.Identifier("my#%#e123!#!@TAG"),
                  Expression.Not(Expression.Identifier("____9123231...."))
               ),
               Expression.Identifier("'''',.''/'l/'l/''/''''")
            ),
            Expression.Identifier("thattag")
         )
      }
   }
}
