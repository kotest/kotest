package com.sksamuel.kotest.engine.launcher

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.engine.reporter.TeamCityMessages
import io.kotest.matchers.shouldBe

class TeamCityMessagesTest : ShouldSpec({

   should("escape brackets in messages") {
      val msg = TeamCityMessages.testFailed("testcity", "escape brackets")
         .message("expected:<[foo]> but was:<[bar]>")
         .duration(67)
         .toString()
      msg shouldBe """testcity[testFailed name='escape brackets' message='expected:<|[foo|]> but was:<|[bar|]>' duration='67']"""
   }

   should("escape quotes in messages") {
      val msg = TeamCityMessages.testFailed("testcity", "escape quotes")
         .message("foo'bar")
         .duration(67)
         .toString()
      msg shouldBe """testcity[testFailed name='escape quotes' message='foo|'bar' duration='67']"""
   }

   should("escape quotes in names") {
      val msg = TeamCityMessages.testFailed("testcity", "isn't busy")
         .message("foo'bar")
         .duration(67)
         .toString()
      msg shouldBe """testcity[testFailed name='isn|'t busy' message='foo|'bar' duration='67']"""
   }

   should("escape new lines") {
      val msg = TeamCityMessages.testFailed("testcity", "escape brackets")
         .message(
            """
qweqwe
ewr
ret
"""
         )
         .duration(67)
         .toString()
      msg shouldBe """testcity[testFailed name='escape brackets' message='qweqwe|newr|nret' duration='67']"""
   }

   should("support comparison values") {
      val msg = TeamCityMessages.testFailed("testcity", "support comparison values")
         .type("comparisonFailure")
         .message("test failed")
         .actual("act")
         .expected("exp")
         .duration(44)
         .toString()
      msg shouldBe """testcity[testFailed name='support comparison values' type='comparisonFailure' message='test failed' actual='act' expected='exp' duration='44']"""
   }
})
