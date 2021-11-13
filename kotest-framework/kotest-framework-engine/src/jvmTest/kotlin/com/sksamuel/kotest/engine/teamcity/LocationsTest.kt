package com.sksamuel.kotest.engine.teamcity

import io.kotest.core.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.teamcity.Locations
import io.kotest.matchers.shouldBe

class LocationsTest : FunSpec({

   test("ClassSource hint") {
      Locations.location(SourceRef.ClassSource("foo.bar")) shouldBe "kotest:class://foo.bar:1"
   }

   test("ClassLineSource hint") {
      Locations.location(SourceRef.ClassLineSource("foo.bar", 34)) shouldBe "kotest:class://foo.bar:34"
   }

   test("FileSource hint") {
      Locations.location(SourceRef.FileSource("foo.kt")) shouldBe "kotest:file://foo.kt:1"
   }

   test("FileLineSource hint") {
      Locations.location(SourceRef.FileLineSource("foo.kt", 34)) shouldBe "kotest:file://foo.kt:34"
   }
})
