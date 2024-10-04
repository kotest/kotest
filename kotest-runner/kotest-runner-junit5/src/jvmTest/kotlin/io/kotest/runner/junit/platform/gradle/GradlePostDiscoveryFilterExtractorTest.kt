package io.kotest.runner.junit.platform.gradle

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.gradle.api.internal.tasks.testing.filter.TestFilterSpec
import org.gradle.api.internal.tasks.testing.filter.TestSelectionMatcher

@EnabledIf(LinuxCondition::class)
class GradlePostDiscoveryFilterExtractorTest : FunSpec({

   test("matcher logic") {

      val spec = TestFilterSpec(
         setOf("ClassA", "ClassB.test name"),
         setOf("ExcludedClassA"),
         emptySet()
      )

      val matcher = TestSelectionMatcher(spec)
      matcher.mayIncludeClass("ClassA") shouldBe true
      matcher.mayIncludeClass("ClassB") shouldBe true
      matcher.matchesTest("ClassB", "test name") shouldBe true
      matcher.matchesTest("ClassB", "test name 2") shouldBe false
      matcher.mayIncludeClass("ClassC") shouldBe false
      matcher.mayIncludeClass("ExcludedClassA") shouldBe false
   }

   test("extract regexes from build script") {

      val spec = TestFilterSpec(
         setOf("ClassA", "ClassB.test name"),
         setOf("ExcludedClassA"),
         emptySet()
      )

      val matcher = TestSelectionMatcher(spec)

      val filter =
         Class.forName("org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor\$ClassMethodNameFilter")
            .declaredConstructors.first { it.parameterCount == 1 }.let {
               it.isAccessible = true
               it.newInstance(matcher)
            }
      GradlePostDiscoveryFilterExtractor.extract(listOf(filter)) shouldBe listOf(
         "\\QClassA\\E",
         "\\QClassB.test name\\E"
      )
   }

})
