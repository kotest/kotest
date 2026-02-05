package com.sksamuel.kotest.engine.teamcity

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.teamcity.TeamCityPathRenderer
import io.kotest.engine.test.names.DisplayNameFormatting
import io.kotest.matchers.shouldBe

class TeamCityPathRendererTest : FreeSpec() {
   init {
      "render spec ref " {
         TeamCityPathRenderer(DisplayNameFormatting(null))
            .testPath(SpecRef.Reference(TeamCityPathRendererTest::class)) shouldBe "com.sksamuel.kotest.engine.teamcity.TeamCityPathRendererTest"
      }
      "render root tests should include class" {
         val tc = TestCase(
            TeamCityPathRendererTest::class.toDescriptor().append("foo"),
            TestNameBuilder.builder("foo").build(),
            TeamCityPathRendererTest(),
            {},
            SourceRef.None,
            TestType.Test
         )
         val renderer = TeamCityPathRenderer(DisplayNameFormatting(null))
         renderer.testPath(SpecRef.Reference(TeamCityPathRendererTest::class))
         renderer.testPath(tc) shouldBe "com.sksamuel.kotest.engine.teamcity.TeamCityPathRendererTest.foo"
      }
      "render nested tests should be flattened" {
         val parent = TestCase(
            TeamCityPathRendererTest::class.toDescriptor().append("foo"),
            TestNameBuilder.builder("foo").build(),
            TeamCityPathRendererTest(),
            {},
            SourceRef.None,
            TestType.Test,
            parent = null
         )
         val tc = TestCase(
            parent.descriptor.append("bar"),
            TestNameBuilder.builder("bar").build(),
            TeamCityPathRendererTest(),
            {},
            SourceRef.None,
            TestType.Test,
            parent = parent
         )
         val renderer = TeamCityPathRenderer(DisplayNameFormatting(null))
         renderer.testPath(SpecRef.Reference(TeamCityPathRendererTest::class))
         renderer.testPath(tc) shouldBe "com.sksamuel.kotest.engine.teamcity.TeamCityPathRendererTest.foo ⇢ bar"
      }
      "periods should be relaced in test names" {
         val parent = TestCase(
            TeamCityPathRendererTest::class.toDescriptor().append("foo.bar"),
            TestNameBuilder.builder("foo.bar").build(),
            TeamCityPathRendererTest(),
            {},
            SourceRef.None,
            TestType.Test,
            parent = null
         )
         val tc = TestCase(
            parent.descriptor.append("boo.far"),
            TestNameBuilder.builder("boo.far").build(),
            TeamCityPathRendererTest(),
            {},
            SourceRef.None,
            TestType.Test,
            parent = parent
         )
         val renderer = TeamCityPathRenderer(DisplayNameFormatting(null))
         renderer.testPath(SpecRef.Reference(TeamCityPathRendererTest::class))
         renderer.testPath(tc) shouldBe "com.sksamuel.kotest.engine.teamcity.TeamCityPathRendererTest.foo bar ⇢ boo far"
      }
   }
}
