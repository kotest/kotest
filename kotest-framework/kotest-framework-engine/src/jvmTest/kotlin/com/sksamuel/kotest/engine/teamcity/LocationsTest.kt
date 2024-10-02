package com.sksamuel.kotest.engine.teamcity

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.teamcity.Locations
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class LocationsTest : FunSpec({

   test("ClassSource hint") {
      Locations.location(SourceRef.ClassSource("foo.bar", null)) shouldBe "kotest:class://foo.bar:1"
      Locations.location(SourceRef.ClassSource("foo.bar", 34)) shouldBe "kotest:class://foo.bar:34"
   }

   test("FileSource hint") {
      Locations.location(SourceRef.FileSource("foo.kt", null)) shouldBe "kotest:file://foo.kt:1"
      Locations.location(SourceRef.FileSource("foo.kt", 34)) shouldBe "kotest:file://foo.kt:34"
   }
})
