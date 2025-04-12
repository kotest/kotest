package com.sksamuel.kotest.engine.teamcity

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.teamcity.Locations
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class LocationsTest : FunSpec({

   test("ClassSource hint") {
      Locations.location(SourceRef.ClassSource("foo.bar", null)) shouldBe "kotest://foo.bar:1"
      Locations.location(SourceRef.ClassSource("foo.bar", 34)) shouldBe "kotest://foo.bar:34"
   }
})
