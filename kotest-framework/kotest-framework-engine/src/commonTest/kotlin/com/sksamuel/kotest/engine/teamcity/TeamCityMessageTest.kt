package com.sksamuel.kotest.engine.teamcity

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.engine.teamcity.TeamCityMessage
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds

class TeamCityMessageTest : ShouldSpec({

   should("escape brackets in messages") {
      TeamCityMessage("testcity", TeamCityMessage.Types.TEST_STARTED) {
         name("escape brackets")
         message("expected:<[foo]> but was:<[bar]>")
      }.build() shouldBe """testcity[testFailed name='escape brackets' message='expected:<|[foo|]> but was:<|[bar|]>' duration='67']"""
   }

   should("escape quotes in messages") {
      TeamCityMessage("testcity", TeamCityMessage.Types.TEST_STARTED) {
         message("foo'bar")
         duration(67.milliseconds)
      }.build() shouldBe """testcity[testFailed name='escape quotes' message='foo|'bar' duration='67']"""
   }

   should("escape quotes in names") {
      TeamCityMessage("testcity", TeamCityMessage.Types.TEST_STARTED) {
         name("isn't busy")
      }.build() shouldBe """testcity[testFailed name='isn|'t busy' message='foo|'bar' duration='67']"""
   }

   should("escape new lines") {
      TeamCityMessage("testcity", TeamCityMessage.Types.TEST_STARTED) {
         message(
            """
qweqwe
ewr
ret
"""
         )
      }.build() shouldBe """testcity[testFailed name='escape brackets' message='qweqwe|newr|nret' duration='67']"""
   }

   should("support comparison values") {
      TeamCityMessage("testcity", TeamCityMessage.Types.TEST_STARTED) {
         type("comparisonFailure")
         message("test failed")
         actual("act")
         expected("exp")
      }.build() shouldBe """testcity[testFailed name='support comparison values' type='comparisonFailure' message='test failed' actual='act' expected='exp' duration='44']"""
   }
})
