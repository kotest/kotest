package com.sksamuel.kotest.engine.test.names

import io.kotest.assertions.assertSoftly
import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.annotation.DisplayName
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.Tags
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.IncludeTestScopeAffixes
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.core.descriptors.toDescriptor
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.tags.TagExpression
import io.kotest.engine.test.names.DefaultDisplayNameFormatter
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

@Isolate
@EnabledIf(LinuxOnlyGithubCondition::class)
class DefaultDisplayNameFormatterTest : FunSpec() {
   init {

      test("@DisplayName should be used for spec name") {
         DefaultDisplayNameFormatter().format(SpecWithDisplayName::class) shouldBe "ZZZZZ"
      }

      test("test name should use full path option") {

         val c = object : AbstractProjectConfig() {
            override val displayFullTestPath = true
         }

         val tc1 = TestCase(
            SpecWithDisplayName::class.toDescriptor().append("test"),
            TestNameBuilder.builder("test").build(),
            SpecWithDisplayName(),
            {},
            SourceRef.None,
            TestType.Test,
         )
         val tc2 = TestCase(
            SpecWithDisplayName::class.toDescriptor().append("test2"),
            TestNameBuilder.builder("test2").build(),
            SpecWithDisplayName(),
            {},
            SourceRef.None,
            TestType.Test,
            parent = tc1
         )
         DefaultDisplayNameFormatter(ProjectConfigResolver(c), TestConfigResolver(c)).format(tc2) shouldBe "test test2"
      }

      test("tags should be appended from config when configuration is set") {

         val c = object : AbstractProjectConfig() {
            override val testNameAppendTags = true
         }

         val tc = TestCase(
            SpecWithDisplayName::class.toDescriptor().append("test"),
            TestNameBuilder.builder("test").build(),
            SpecWithDisplayName(),
            {},
            SourceRef.None,
            TestType.Test,
            TestConfig(tags = setOf(NamedTag("Foo"), Dummy))
         )
         DefaultDisplayNameFormatter(
            ProjectConfigResolver(c),
            TestConfigResolver(c)
         ).format(tc) shouldBe "test[tags = Foo, Dummy]"
      }

      test("bang should not be included in test name") {

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("!test"),
            name = TestNameBuilder.builder("!test").build(),
            spec = SpecWithDisplayName(),
            test = {},
            source = SourceRef.None,
            type = TestType.Test,
            config = TestConfig(tags = setOf(Dummy, NoUse))
         )

         DefaultDisplayNameFormatter().format(tc) shouldBe "test"
      }

      test("focus should not be included in test name") {

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("f:test"),
            name = TestNameBuilder.builder("f:test").build(),
            spec = SpecWithDisplayName(),
            test = {},
            source = SourceRef.None,
            type = TestType.Test,
            config = TestConfig(tags = setOf(Dummy, NoUse))
         )

         DefaultDisplayNameFormatter().format(tc) shouldBe "test"
      }

      test("name should include prefix if affixes are included by default") {

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("f:test"),
            name = TestNameBuilder.builder("foo").withPrefix("prefix").withDefaultAffixes().build(),
            spec = SpecWithDisplayName(),
            test = {},
            source = SourceRef.None,
            type = TestType.Test,
            config = TestConfig(tags = setOf(Dummy, NoUse))
         )

         DefaultDisplayNameFormatter().format(tc) shouldBe "prefixfoo"
      }

      test("name should include prefix if affixes are excluded by default but enabled by config") {

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("f:test"),
            name = TestNameBuilder.builder("foo").withPrefix("prefix").build(),
            spec = SpecWithDisplayName(),
            test = {},
            source = SourceRef.None,
            type = TestType.Test,
            config = TestConfig(tags = setOf(Dummy, NoUse))
         )
         val c = object : AbstractProjectConfig() {
            override val includeTestScopeAffixes = IncludeTestScopeAffixes.ALWAYS
         }
         DefaultDisplayNameFormatter(ProjectConfigResolver(c), TestConfigResolver(c)).format(tc) shouldBe "prefixfoo"
      }

      test("name should include suffix if affixes are included by default") {

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("f:test"),
            name = TestNameBuilder.builder("foo").withSuffix("suffix").withDefaultAffixes().build(),
            spec = SpecWithDisplayName(),
            test = {},
            source = SourceRef.None,
            type = TestType.Test,
            config = TestConfig(tags = setOf(Dummy, NoUse))
         )

         DefaultDisplayNameFormatter().format(tc) shouldBe "foosuffix"
      }

      test("name should include suffix if affixes are excluded by default but enabled in config") {

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("f:test"),
            name = TestNameBuilder.builder("foo").withSuffix("suffix").build(),
            spec = SpecWithDisplayName(),
            test = {},
            source = SourceRef.None,
            type = TestType.Test,
            config = TestConfig(tags = setOf(Dummy, NoUse))
         )

         val c = object : AbstractProjectConfig() {
            override val includeTestScopeAffixes = IncludeTestScopeAffixes.ALWAYS
         }
         DefaultDisplayNameFormatter(ProjectConfigResolver(c), TestConfigResolver(c)).format(tc) shouldBe "foosuffix"
      }

      test("Tags from Spec are only added once when displaying the name of the test with tags included") {

         val c = object : AbstractProjectConfig() {
            override val testNameAppendTags = true
         }

         val collector = TeamCityTestEngineListener()

         val report = captureStandardOut {
            TestEngineLauncher(collector)
               .withProjectConfig(c)
               .withClasses(TaggedSpec::class)
               .withTagExpression(TagExpression.Empty)
               .launch()
         }

         assertSoftly(report) {
            shouldContain("Bar|[tags = A|]")
            shouldContain("Foo|[tags = A|]")
         }
      }
   }
}

@Tags("A")
private class TaggedSpec : FunSpec({
   context("Foo") {
      test("Bar") {

      }
   }
})

private object Dummy : Tag()
private object NoUse : Tag()

@DisplayName("ZZZZZ")
private class SpecWithDisplayName : FunSpec({
   test("a") { }
})
