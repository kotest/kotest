package io.kotest.runner.junit.platform.gradle

import io.kotest.common.reflection.bestName
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.extensions.filter.DescriptorFilterResult
import io.kotest.engine.extensions.filter.INCLUDE_PATTERN_ENV
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class GradleClassMethodRegexTestFilterTest : FunSpec({

   context("include classes") {
      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      withData(
         nameFn = { filters -> "should be INCLUDED when evaluating $filters" },
         setOf("\\QGradleClassMethodRegexTestFilterTest\\E"),
         setOf(".*\\QthodRegexTestFilterTest\\E"),
         setOf(".*\\QTest\\E"),
         setOf("\\Qio.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilterTest\\E"),
         setOf(".*\\Q.platform.gradle.GradleClassMethodRegexTestFilterTest\\E"),
         setOf(".*\\Qorm.gradle.GradleClassMethodRegexTestFilterTest\\E")
      ) { filters ->
         GradleClassMethodRegexTestFilter(filters).filter(spec) shouldBe DescriptorFilterResult.Include
      }
   }

   test("wildcard prefix") {
      val foo = Descriptor.SpecDescriptor(DescriptorId("org.package.Foo"))
      val bar = Descriptor.SpecDescriptor(DescriptorId("org.package.Bar"))
      GradleClassMethodRegexTestFilter(setOf(".*.*\\QFoo\\E")).filter(foo) shouldBe DescriptorFilterResult.Include
      GradleClassMethodRegexTestFilter(setOf(".*.*\\QFoo\\E")).filter(bar) shouldBe DescriptorFilterResult.Exclude(null)
   }

   context("exclude classes") {
      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      withData(
         nameFn = { filters -> "should be EXCLUDED when evaluating $filters" },
         setOf("\\QGradleClassMethodRegexTestFilterTest2\\E"),
         setOf("\\QGradleClassMethodRegexTestFilterTes\\E"),
         setOf("\\Qio.kotest.runner.junit.platform.GradleClassMethodRegexTestFilterTest\\E")
      ) { filters ->
         GradleClassMethodRegexTestFilter(filters).filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
      }
   }

   context("include packages") {

      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      val container = spec.append("a context")
      val test = container.append("nested test")

      withData(
         nameFn = { filters -> "should be INCLUDED if any of the filters matches when evaluating $filters" },
         setOf("\\Qio.kotest.runner.junit.platform.gradle\\E"),
         setOf("\\Qio.kotest.runner.junit.platform.gradle.\\E.*"),
         setOf(".*\\Qnner.junit.platform.gradle\\E"),
         setOf(".*\\Qnner.junit.platform.gradle.\\E.*"),
         setOf(".*\\Q.junit.platform.gradle\\E"),
         setOf("\\Qio.kotest.runner.junit.platform.gra\\E.*"),
         setOf(".*\\QNotSpec\\E", "\\Qio.kotest.runner.junit\\E"),
      ) { filters ->
         GradleClassMethodRegexTestFilter(filters).filter(spec) shouldBe DescriptorFilterResult.Include
      }

      withData(
         nameFn = { filters -> "should be EXCLUDED if none of the filters matches when evaluating $filters" },
         setOf("\\Qio.kotest.runner.junit2\\E"),
         setOf("\\Qio.kotest.runner.junit2\\E", ".*\\QNotSpec\\E"),
      ) { filters ->
         GradleClassMethodRegexTestFilter(filters).filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
      }

      withData(
         nameFn = { filters -> "should be INCLUDED when container and test were evaluated using $filters" },
         setOf("\\QGradleClassMethodRegexTestFilterTest.a context\\E.*"),
         setOf(".*\\QTest\\E", "\\QGradleClassMethodRegex\\E.*\\Q.a context\\E.*"),
      ) { filters ->
         GradleClassMethodRegexTestFilter(filters).filter(container) shouldBe DescriptorFilterResult.Include
         GradleClassMethodRegexTestFilter(filters).filter(test) shouldBe DescriptorFilterResult.Include
      }
   }

   context("includes with test paths") {

      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      val container = spec.append("a context")
      val test = container.append("nested test")
      val fqcn = "\\Q${GradleClassMethodRegexTestFilterTest::class.qualifiedName}\\E"

      withData(
         nameFn = { "should be INCLUDED when filter is: $it" },
         fqcn,
         "$fqcn\\Q.a context\\E.*",
         ".*\\Q.gradle.GradleClassMethodRegexTestFilterTest.a context\\E.*",
         ".*\\Qadle.GradleClassMethodRegexTestFilterTest.a context\\E.*"
      ) { filter ->
         GradleClassMethodRegexTestFilter(setOf(filter))
            .filter(test) shouldBe DescriptorFilterResult.Include
      }

      withData(
         nameFn = { "should be EXCLUDED when filter is: $it" },
         "$fqcn\\Q.a context2\\E",
         "$fqcn\\Q.nested test\\E",
         "$fqcn\\Q.a context.nested test2\\E",
         ".*\\QsMethodRegexTestFilterTest.a context -- nested test2\\Q",
      ) { filter ->
         GradleClassMethodRegexTestFilter(setOf(filter))
            .filter(test) shouldBe DescriptorFilterResult.Exclude(null)
      }
   }

   context("nested tests should be included when filtering to parent context without wildcard") {
      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      val container = spec.append("a context")
      val nestedTest = container.append("nested test")
      val fqn = "\\Q${GradleClassMethodRegexTestFilterTest::class.qualifiedName}\\E"

      test("nested test should be INCLUDED when filtering to parent context") {
         // This is the exact pattern gradle would generate for --tests 'FQN.a context'
         val filter = "$fqn\\Q.a context\\E"
         GradleClassMethodRegexTestFilter(setOf(filter))
            .filter(nestedTest) shouldBe DescriptorFilterResult.Include
      }

      test("parent context should be INCLUDED when filtering to it") {
         val filter = "$fqn\\Q.a context\\E"
         GradleClassMethodRegexTestFilter(setOf(filter))
            .filter(container) shouldBe DescriptorFilterResult.Include
      }

      test("deeply nested test should be INCLUDED when filtering to grandparent context") {
         val deeplyNestedTest = nestedTest.append("deeply nested")
         val filter = "$fqn\\Q.a context\\E"
         GradleClassMethodRegexTestFilter(setOf(filter))
            .filter(deeplyNestedTest) shouldBe DescriptorFilterResult.Include
      }
   }

   context("line breaks in test names are normalized") {
      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      val fqn = "\\Q${GradleClassMethodRegexTestFilterTest::class.qualifiedName}\\E"

      test("test with CR in name matches filter with space") {
         val testDescriptor = spec.append("a test\rwith cr")
         val filter = "$fqn\\Q.a test with cr\\E"
         GradleClassMethodRegexTestFilter(setOf(filter)).filter(testDescriptor) shouldBe DescriptorFilterResult.Include
      }

      test("nested test with CR in name matches filter with space") {
         val container = spec.append("parent\rcontext")
         val testDescriptor = container.append("child\rtest")
         val filter = "$fqn\\Q.parent context -- child test\\E"
         GradleClassMethodRegexTestFilter(setOf(filter)).filter(testDescriptor) shouldBe DescriptorFilterResult.Include
      }
   }
   
   context("simple class name with leading wildcard") {
      // When running: ./gradlew test --tests '*DecoderUtilsTest'
      // Gradle converts '*ClassName' to the regex pattern: .*.*\QClassName\E
      // See: https://github.com/kotest/kotest/issues/5639

      val decoderSpec = Descriptor.SpecDescriptor(DescriptorId("org.example.binaries.DecoderUtilsTest"))
      val otherSpec = Descriptor.SpecDescriptor(DescriptorId("org.example.binaries.EncoderUtilsTest"))
      val noPackageSpec = Descriptor.SpecDescriptor(DescriptorId("DecoderUtilsTest"))

      test("spec with deep package path should be INCLUDED by wildcard prefix pattern") {
         GradleClassMethodRegexTestFilter(setOf(".*.*\\QDecoderUtilsTest\\E")).filter(decoderSpec) shouldBe DescriptorFilterResult.Include
      }

      test("non-matching spec should be EXCLUDED by wildcard prefix pattern") {
         GradleClassMethodRegexTestFilter(setOf(".*.*\\QDecoderUtilsTest\\E")).filter(otherSpec) shouldBe DescriptorFilterResult.Exclude(null)
      }

      test("spec with no package should be INCLUDED by wildcard prefix pattern") {
         GradleClassMethodRegexTestFilter(setOf(".*.*\\QDecoderUtilsTest\\E")).filter(noPackageSpec) shouldBe DescriptorFilterResult.Include
      }

      test("nested test within matching spec should be INCLUDED") {
         val nestedTest = decoderSpec.append("decode a number")
         GradleClassMethodRegexTestFilter(setOf(".*.*\\QDecoderUtilsTest\\E")).filter(nestedTest) shouldBe DescriptorFilterResult.Include
      }

      test("wildcard suffix pattern should match any spec ending with Test") {
         val fooTest = Descriptor.SpecDescriptor(DescriptorId("org.example.FooTest"))
         val barTest = Descriptor.SpecDescriptor(DescriptorId("com.example.deep.BarTest"))
         val notATest = Descriptor.SpecDescriptor(DescriptorId("org.example.FooSpec"))
         GradleClassMethodRegexTestFilter(setOf(".*.*\\QTest\\E")).filter(fooTest) shouldBe DescriptorFilterResult.Include
         GradleClassMethodRegexTestFilter(setOf(".*.*\\QTest\\E")).filter(barTest) shouldBe DescriptorFilterResult.Include
         GradleClassMethodRegexTestFilter(setOf(".*.*\\QTest\\E")).filter(notATest) shouldBe DescriptorFilterResult.Exclude(null)
      }
   }

   context("wildcards in the middle of test names") {
      // When test names contain periods (e.g. "test with 1.2.3"), GradleTestFilterBuilder replaces
      // them with wildcards, producing a filter like "MySpec.test with 1*2*3".
      // Gradle converts that to the regex: \QMySpec.test with 1\E.*\Q2\E.*\Q3\E
      // These tests verify that the filter correctly includes/excludes the right descriptors.

      val spec = Descriptor.SpecDescriptor(DescriptorId("io.pkg.MySpec"))

      test("test with period in name is INCLUDED when filter has wildcard in middle") {
         // "test with 1.2.3" → filter "MySpec.test with 1*2*3" → \QMySpec.test with 1\E.*\Q2\E.*\Q3\E
         val testDescriptor = spec.append("test with 1.2.3")
         val filter = "\\QMySpec.test with 1\\E.*\\Q2\\E.*\\Q3\\E"
         GradleClassMethodRegexTestFilter(setOf(filter)).filter(testDescriptor) shouldBe DescriptorFilterResult.Include
      }

      test("test with multiple periods is INCLUDED when filter has multiple wildcards in middle") {
         // "assert a.b equals c.d.e" → filter "MySpec.assert a*b equals c*d*e"
         // → \QMySpec.assert a\E.*\Qb equals c\E.*\Qd\E.*\Qe\E
         val testDescriptor = spec.append("assert a.b equals c.d.e")
         val filter = "\\QMySpec.assert a\\E.*\\Qb equals c\\E.*\\Qd\\E.*\\Qe\\E"
         GradleClassMethodRegexTestFilter(setOf(filter)).filter(testDescriptor) shouldBe DescriptorFilterResult.Include
      }

      test("unrelated test is EXCLUDED when filter has wildcards matching a different test name") {
         val testDescriptor = spec.append("unrelated test")
         val filter = "\\QMySpec.test with 1\\E.*\\Q2\\E.*\\Q3\\E"
         GradleClassMethodRegexTestFilter(setOf(filter)).filter(testDescriptor) shouldBe DescriptorFilterResult.Exclude(null)
      }

      test("FQN filter with wildcards in middle of test name is INCLUDED") {
         // --tests 'io.pkg.MySpec.test with 1*2*3' → \Qio.pkg.MySpec.test with 1\E.*\Q2\E.*\Q3\E
         val testDescriptor = spec.append("test with 1.2.3")
         val filter = "\\Qio.pkg.MySpec.test with 1\\E.*\\Q2\\E.*\\Q3\\E"
         GradleClassMethodRegexTestFilter(setOf(filter)).filter(testDescriptor) shouldBe DescriptorFilterResult.Include
      }

      test("wildcard-prefix filter with wildcards in middle of test name is INCLUDED") {
         // --tests '*MySpec.test with 1*2*3' → .*.*\QMySpec.test with 1\E.*\Q2\E.*\Q3\E
         val testDescriptor = spec.append("test with 1.2.3")
         val filter = ".*.*\\QMySpec.test with 1\\E.*\\Q2\\E.*\\Q3\\E"
         GradleClassMethodRegexTestFilter(setOf(filter)).filter(testDescriptor) shouldBe DescriptorFilterResult.Include
      }

      test("nested test with periods in both context and test name is INCLUDED") {
         // context "context v1.0", test "name with periods 1.2.3 and more"
         // filter: "MySpec.context v1*0 -- name with periods 1*2*3 and more"
         // → \QMySpec.context v1\E.*\Q0 -- name with periods 1\E.*\Q2\E.*\Q3 and more\E
         val container = spec.append("context v1.0")
         val testDescriptor = container.append("name with periods 1.2.3 and more")
         val filter = "\\QMySpec.context v1\\E.*\\Q0 -- name with periods 1\\E.*\\Q2\\E.*\\Q3 and more\\E"
         GradleClassMethodRegexTestFilter(setOf(filter)).filter(testDescriptor) shouldBe DescriptorFilterResult.Include
      }
   }

   // Unable to make field final java.util.Map java.util.Collections$UnmodifiableMap.m accessible: module java.base does not "opens java.util" to unnamed module @62163b39
   test("!is ignored when KOTEST_INCLUDE_PATTERN is set") {
      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      val container = spec.append("a context")
      val test = container.append("nested test")
      withEnvironment(INCLUDE_PATTERN_ENV, "foo") {
         GradleClassMethodRegexTestFilter(setOf("io.nothing")).filter(test) shouldBe DescriptorFilterResult.Include
      }
   }
})
