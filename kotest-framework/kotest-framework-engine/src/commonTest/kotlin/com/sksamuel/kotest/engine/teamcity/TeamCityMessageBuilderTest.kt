package com.sksamuel.kotest.engine.teamcity

import io.kotest.assertions.KotestAssertionFailedError
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.engine.teamcity.TeamCityMessageBuilder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class TeamCityMessageBuilderTest : ShouldSpec({

   should("escape brackets in messages") {
      val msg = TeamCityMessageBuilder.testFailed("testcity", "escape brackets")
         .message("expected:<[foo]> but was:<[bar]>")
         .duration(67.milliseconds)
         .build()
      msg shouldBe """testcity[testFailed name='escape brackets' message='expected:<|[foo|]> but was:<|[bar|]>' duration='67']"""
   }

   should("escape quotes in messages") {
      val msg = TeamCityMessageBuilder.testFailed("testcity", "escape quotes")
         .message("foo'bar")
         .duration(67.milliseconds)
         .build()
      msg shouldBe """testcity[testFailed name='escape quotes' message='foo|'bar' duration='67']"""
   }

   should("escape quotes in names") {
      val msg = TeamCityMessageBuilder.testFailed("testcity", "isn't busy")
         .message("foo'bar")
         .duration(67.milliseconds)
         .build()
      msg shouldBe """testcity[testFailed name='isn|'t busy' message='foo|'bar' duration='67']"""
   }

   should("escape new lines") {
      val msg = TeamCityMessageBuilder.testFailed("testcity", "escape brackets")
         .message(
            """
qweqwe
ewr
ret
"""
         )
         .duration(67.milliseconds)
         .build()
      msg shouldBe """testcity[testFailed name='escape brackets' message='qweqwe|newr|nret' duration='67']"""
   }

   should("support comparison values") {
      val msg = TeamCityMessageBuilder.testFailed("testcity", "support comparison values")
         .type("comparisonFailure")
         .message("test failed")
         .actual("act")
         .expected("exp")
         .duration(44.milliseconds)
         .build()
      msg shouldBe """testcity[testFailed name='support comparison values' type='comparisonFailure' message='test failed' actual='act' expected='exp' duration='44']"""
   }

   should("do not set comparison values if not provided") {

      val msg = TeamCityMessageBuilder.testFailed("testcity", "support comparison values")
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(4.seconds)
         .withException(KotestAssertionFailedError("foo", null, null, null))
         .build()
      msg.shouldNotContain("comparisonFailure")
   }
})
