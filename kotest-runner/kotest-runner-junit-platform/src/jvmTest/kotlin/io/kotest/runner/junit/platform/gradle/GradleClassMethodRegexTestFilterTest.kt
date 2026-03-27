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

   context("question-mark wildcards in test names match tests with periods") {
      // GradleTestFilterBuilder replaces '.' in test names with '?' to avoid Gradle
      // misinterpreting them as FQN separators. Gradle converts '?' -> '.' (regex any-char)
      // and wraps surrounding literals in \Q...\E, so the filter for test name "1.2.3 my test"
      // becomes the regex pattern \Q...MySpec.1\E.\Q2\E.\Q3 my test\E.

      val spec = Descriptor.SpecDescriptor(DescriptorId("io.example.MySpec"))
      val fqn = "\\Qio.example.MySpec\\E"

      test("root test named '1.2.3 my test' is INCLUDED by question-mark-escaped filter") {
         val testDescriptor = spec.append("1.2.3 my test")
         // Gradle converts --tests 'io.example.MySpec.1?2?3 my test' to this regex:
         val filter = "$fqn\\Q.1\\E.\\Q2\\E.\\Q3 my test\\E"
         GradleClassMethodRegexTestFilter(setOf(filter)).filter(testDescriptor) shouldBe DescriptorFilterResult.Include
      }

      test("root test named '1.2.3 my test' is INCLUDED by spec-level filter") {
         val testDescriptor = spec.append("1.2.3 my test")
         GradleClassMethodRegexTestFilter(setOf(fqn)).filter(testDescriptor) shouldBe DescriptorFilterResult.Include
      }

      test("root test with a different name is EXCLUDED by question-mark-escaped filter") {
         val testDescriptor = spec.append("4.5.6 other test")
         val filter = "$fqn\\Q.1\\E.\\Q2\\E.\\Q3 my test\\E"
         GradleClassMethodRegexTestFilter(setOf(filter)).filter(testDescriptor) shouldBe DescriptorFilterResult.Exclude(null)
      }

      test("nested test under '1.2.3 my test' is INCLUDED when filtering to parent") {
         val parent = spec.append("1.2.3 my test")
         val child = parent.append("nested child")
         val filter = "$fqn\\Q.1\\E.\\Q2\\E.\\Q3 my test\\E"
         GradleClassMethodRegexTestFilter(setOf(filter)).filter(child) shouldBe DescriptorFilterResult.Include
      }

      test("context named 'v1.0 context' with nested test is INCLUDED by question-mark-escaped filter") {
         val container = spec.append("v1.0 context")
         val child = container.append("feature 2.0")
         // Gradle converts --tests 'io.example.MySpec.v1?0 context -- feature 2?0'
         val filter = "$fqn\\Q.v1\\E.\\Q0 context -- feature 2\\E.\\Q0\\E"
         GradleClassMethodRegexTestFilter(setOf(filter)).filter(child) shouldBe DescriptorFilterResult.Include
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
