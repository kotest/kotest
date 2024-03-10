package com.sksamuel.kotest.engine.test.names

import io.kotest.assertions.assertSoftly
import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.TagExpression
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.Tags
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.test.config.ResolvedTestConfig
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.test.names.DefaultDisplayNameFormatter
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

@Isolate
class DefaultDisplayNameFormatterTest : FunSpec() {
   init {

      test("@DisplayName should be used for spec name") {
         DefaultDisplayNameFormatter(ProjectConfiguration()).format(SpecWithDisplayName::class) shouldBe "ZZZZZ"
      }

      test("test name should use full path option") {
         val conf = ProjectConfiguration()
         conf.displayFullTestPath = true
         val tc1 = TestCase(
            SpecWithDisplayName::class.toDescriptor().append("test"),
            TestName("test"),
            SpecWithDisplayName(),
            {},
            sourceRef(),
            TestType.Test,
         )
         val tc2 = TestCase(
            SpecWithDisplayName::class.toDescriptor().append("test2"),
            TestName("test2"),
            SpecWithDisplayName(),
            {},
            sourceRef(),
            TestType.Test,
            parent = tc1
         )
         DefaultDisplayNameFormatter(conf).format(tc2) shouldBe "test test2"
      }

      test("tags should be appended from config when configuration is set") {
         val c = ProjectConfiguration()
         c.testNameAppendTags = true

         val tc = TestCase(
            SpecWithDisplayName::class.toDescriptor().append("test"),
            TestName("test"),
            SpecWithDisplayName(),
            {},
            sourceRef(),
            TestType.Test,
            ResolvedTestConfig.default.copy(tags = setOf(NamedTag("Foo"), Dummy))
         )
         DefaultDisplayNameFormatter(c).format(tc) shouldBe "test[tags = Foo, Dummy]"
      }

      test("bang should not be included in test name") {

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("!test"),
            name = TestName("!test"),
            spec = SpecWithDisplayName(),
            test = {},
            source = sourceRef(),
            type = TestType.Test,
            config = ResolvedTestConfig.default.copy(tags = setOf(Dummy, NoUse))
         )

         DefaultDisplayNameFormatter(ProjectConfiguration()).format(tc) shouldBe "test"
      }

      test("focus should not be included in test name") {

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("f:test"),
            name = TestName("f:test"),
            spec = SpecWithDisplayName(),
            test = {},
            source = sourceRef(),
            type = TestType.Test,
            config = ResolvedTestConfig.default.copy(tags = setOf(Dummy, NoUse))
         )

         DefaultDisplayNameFormatter(ProjectConfiguration()).format(tc) shouldBe "test"
      }

      test("name should include prefix if affixes are included by default") {

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("f:test"),
            name = TestName("prefix", "foo", null, true),
            spec = SpecWithDisplayName(),
            test = {},
            source = sourceRef(),
            type = TestType.Test,
            config = ResolvedTestConfig.default.copy(tags = setOf(Dummy, NoUse))
         )

         DefaultDisplayNameFormatter(ProjectConfiguration()).format(tc) shouldBe "prefixfoo"
      }

      test("name should include prefix if affixes are excluded by default but enabled by config") {

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("f:test"),
            name = TestName("prefix", "foo", null, false),
            spec = SpecWithDisplayName(),
            test = {},
            source = sourceRef(),
            type = TestType.Test,
            config = ResolvedTestConfig.default.copy(tags = setOf(Dummy, NoUse))
         )

         val c = ProjectConfiguration()
         c.includeTestScopeAffixes = true
         DefaultDisplayNameFormatter(c).format(tc) shouldBe "prefixfoo"
      }

      test("name should include suffix if affixes are included by default") {

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("f:test"),
            name = TestName(null, "foo", "suffix", true),
            spec = SpecWithDisplayName(),
            test = {},
            source = sourceRef(),
            type = TestType.Test,
            config = ResolvedTestConfig.default.copy(tags = setOf(Dummy, NoUse))
         )

         DefaultDisplayNameFormatter(ProjectConfiguration()).format(tc) shouldBe "foosuffix"
      }

      test("name should include suffix if affixes are excluded by default but enabled in config") {

         val tc = TestCase(
            descriptor = SpecWithDisplayName::class.toDescriptor().append("f:test"),
            name = TestName(null, "foo", "suffix", false),
            spec = SpecWithDisplayName(),
            test = {},
            source = sourceRef(),
            type = TestType.Test,
            config = ResolvedTestConfig.default.copy(tags = setOf(Dummy, NoUse))
         )

         val c = ProjectConfiguration()
         c.includeTestScopeAffixes = true
         DefaultDisplayNameFormatter(c).format(tc) shouldBe "foosuffix"
      }

      test("Tags from Spec are only added once when displaying the name of the test with tags included") {
         val configuration = ProjectConfiguration().apply {
            testNameAppendTags = true
            includePrivateClasses = true
         }

         val collector = TeamCityTestEngineListener()

         val report = captureStandardOut {
            TestEngineLauncher(collector)
               .withConfiguration(configuration)
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
