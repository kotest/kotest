package com.sksamuel.kotest.engine.teamcity

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.teamcity.TeamCityWriter
import io.kotest.engine.test.names.DisplayNameFormatting
import io.kotest.matchers.shouldBe

class TeamCityWriterTest : FunSpec() {
   init {

      test("!test name should use embedded locations when enabled") {
         val writer = TeamCityWriter("tc", DisplayNameFormatting(null), true)
         val tc = TestCase(
            TeamCityWriterTest::class.toDescriptor().append("a"),
            TestNameBuilder.builder("a").build(),
            TeamCityWriterTest(),
            {},
            SourceRef.None,
            TestType.Test,
         )
         writer.testName(tc) shouldBe "<kotest>com.sksamuel.kotest.engine.teamcity.TeamCityWriterTest/a</kotest>a"
      }

      test("test name should use the display name only when embedded locations are disabled") {
         val writer = TeamCityWriter("tc", DisplayNameFormatting(null), false)
         val tc = TestCase(
            TeamCityWriterTest::class.toDescriptor().append("a"),
            TestNameBuilder.builder("a").build(),
            TeamCityWriterTest(),
            {},
            SourceRef.None,
            TestType.Test,
         )
         writer.testName(tc) shouldBe "a"
      }
   }
}
