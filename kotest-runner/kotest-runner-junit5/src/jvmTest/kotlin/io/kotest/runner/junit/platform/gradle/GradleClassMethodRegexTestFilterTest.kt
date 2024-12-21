package io.kotest.runner.junit.platform.gradle

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.descriptors.append
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class GradleClassMethodRegexTestFilterTest : FunSpec({

   context("include classes") {
      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      withData(
         nameFn = { filters -> "should be INCLUDED when evaluating $filters" },
         listOf("\\QGradleClassMethodRegexTestFilterTest\\E"),
         listOf(".*\\QthodRegexTestFilterTest\\E"),
         listOf(".*\\QTest\\E"),
         listOf("\\Qio.kotest.runner.junit.platform.gradle.GradleClassMethodRegexTestFilterTest\\E"),
         listOf(".*\\Q.platform.gradle.GradleClassMethodRegexTestFilterTest\\E"),
         listOf(".*\\Qorm.gradle.GradleClassMethodRegexTestFilterTest\\E")
      ) { filters ->
         GradleClassMethodRegexTestFilter(filters).filter(spec) shouldBe TestFilterResult.Include
      }
   }

   context("exclude classes") {
      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      withData(
         nameFn = { filters -> "should be EXCLUDED when evaluating $filters" },
         listOf("\\QGradleClassMethodRegexTestFilterTest2\\E"),
         listOf("\\QGradleClassMethodRegexTestFilterTes\\E"),
         listOf("\\Qio.kotest.runner.junit.platform.GradleClassMethodRegexTestFilterTest\\E")
      ) { filters ->
         GradleClassMethodRegexTestFilter(filters).filter(spec) shouldBe TestFilterResult.Exclude(null)
      }
   }

   context("include packages") {

      val spec = GradleClassMethodRegexTestFilterTest::class.toDescriptor()
      val container = spec.append("a context")
      val test = container.append("nested test")

      withData(
         nameFn = { filters -> "should be INCLUDED if any of the filters matches when evaluating $filters" },
         listOf("\\Qio.kotest.runner.junit.platform.gradle\\E"),
         listOf("\\Qio.kotest.runner.junit.platform.gradle.\\E.*"),
         listOf(".*\\Qnner.junit.platform.gradle\\E"),
         listOf(".*\\Qnner.junit.platform.gradle.\\E.*"),
         listOf(".*\\Q.junit.platform.gradle\\E"),
         listOf("\\Qio.kotest.runner.junit.platform.gra\\E.*"),
         listOf(".*\\QNotSpec\\E", "\\Qio.kotest.runner.junit\\E"),
      ) { filters ->
         GradleClassMethodRegexTestFilter(filters).filter(spec) shouldBe TestFilterResult.Include
      }

      withData(
         nameFn = { filters -> "should be EXCLUDED if none of the filters matches when evaluating $filters" },
         listOf("\\Qio.kotest.runner.junit2\\E"),
         listOf("\\Qio.kotest.runner.junit2\\E", ".*\\QNotSpec\\E"),
      ) { filters ->
         GradleClassMethodRegexTestFilter(filters).filter(spec) shouldBe TestFilterResult.Exclude(null)
      }

      withData(
         nameFn = { filters -> "should be INCLUDED when container and test were evaluated using $filters" },
         listOf("\\QGradleClassMethodRegexTestFilterTest.a context\\E.*"),
         listOf(".*\\QTest\\E", "\\QGradleClassMethodRegex\\E.*\\Q.a context\\E.*"),
      ) { filters ->
         GradleClassMethodRegexTestFilter(filters).filter(container) shouldBe TestFilterResult.Include
         GradleClassMethodRegexTestFilter(filters).filter(test) shouldBe TestFilterResult.Include
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
         GradleClassMethodRegexTestFilter(listOf(filter))
            .filter(test) shouldBe TestFilterResult.Include
      }

      withData(
         nameFn = { "should be EXCLUDED when filter is: $it" },
         "$fqcn\\Q.a context2\\E",
         "$fqcn\\Q.nested test\\E",
         "$fqcn\\Q.a context.nested test2\\E",
         ".*\\QsMethodRegexTestFilterTest.a context -- nested test2\\Q",
      ) { filter ->
         GradleClassMethodRegexTestFilter(listOf(filter))
            .filter(test) shouldBe TestFilterResult.Exclude(null)
      }
   }
})
