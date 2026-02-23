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

   context("test names with newlines or surrounding whitespace are normalized before matching") {
      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      val fqcn = "\\Q${GradleClassMethodRegexTestFilterTest::class.qualifiedName}\\E"

      test("descriptor with newline in test name matches normalized pattern") {
         val testWithNewline = spec.append("line one\nline two")
         val filter = "$fqcn\\Q.line one line two\\E"
         GradleClassMethodRegexTestFilter(setOf(filter))
            .filter(testWithNewline) shouldBe DescriptorFilterResult.Include
      }

      test("descriptor with CRLF in test name matches normalized pattern") {
         val testWithCrlf = spec.append("line one\r\nline two")
         val filter = "$fqcn\\Q.line one line two\\E"
         GradleClassMethodRegexTestFilter(setOf(filter))
            .filter(testWithCrlf) shouldBe DescriptorFilterResult.Include
      }

      test("descriptor with leading and trailing whitespace matches trimmed pattern") {
         val testWithSpaces = spec.append("  foo  ")
         val filter = "$fqcn\\Q.foo\\E"
         GradleClassMethodRegexTestFilter(setOf(filter))
            .filter(testWithSpaces) shouldBe DescriptorFilterResult.Include
      }

      test("descriptor with newline in test name does not match unrelated pattern") {
         val testWithNewline = spec.append("line one\nline two")
         val filter = "$fqcn\\Q.something else\\E"
         GradleClassMethodRegexTestFilter(setOf(filter))
            .filter(testWithNewline) shouldBe DescriptorFilterResult.Exclude(null)
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
