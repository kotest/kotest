package com.sksamuel.kotest.engine.teamcity

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.engine.teamcity.TeamCityMessageBuilder
import io.kotest.matchers.string.shouldNotContain
import kotlin.time.Duration.Companion.seconds

class TeamCityMessageBuilderJvmTest : ShouldSpec({

   should("do not set comparison values if not provided") {

      val msg = TeamCityMessageBuilder.testFailed("testcity", "support comparison values")
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(4.seconds)
         .withException(org.opentest4j.AssertionFailedError("foo", null, null, null))
         .build()
      msg.shouldNotContain("comparisonFailure")
   }
})
