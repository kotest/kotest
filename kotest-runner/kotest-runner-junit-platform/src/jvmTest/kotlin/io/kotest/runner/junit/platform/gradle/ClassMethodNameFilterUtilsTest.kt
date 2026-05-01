package io.kotest.runner.junit.platform.gradle

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.gradle.api.internal.tasks.testing.filter.TestFilterSpec
import org.gradle.api.internal.tasks.testing.filter.TestSelectionMatcher
/**
 * The ClassMethodNameFilter class has moved across Gradle versions:
 * - Gradle 8.x: org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor$ClassMethodNameFilter
 * - Gradle 9.3: org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestDefinitionProcessor$ClassMethodNameFilter
 * - Gradle 9.4+: org.gradle.api.internal.tasks.testing.junitplatform.filters.ClassMethodNameFilter
 *
 * We try all known locations to find the one available on the test classpath.
 */
private val classMethodNameFilterFqns = listOf(
   "org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor\$ClassMethodNameFilter",
   "org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestDefinitionProcessor\$ClassMethodNameFilter",
   "org.gradle.api.internal.tasks.testing.junitplatform.filters.ClassMethodNameFilter",
)

private fun resolveClassMethodNameFilterClass(): Class<*> {
   for (fqn in classMethodNameFilterFqns) {
      runCatching { return Class.forName(fqn) }.getOrNull()
   }
   error("Could not find ClassMethodNameFilter on the classpath. Tried: $classMethodNameFilterFqns")
}

@EnabledIf(LinuxOnlyGithubCondition::class)
class ClassMethodNameFilterUtilsTest : FunSpec({

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

   test("extract regexes from internal gradle class") {

      val spec = TestFilterSpec(
         setOf("ClassA", "ClassB.test name"),
         setOf("ExcludedClassA"),
         emptySet()
      )

      val matcher = TestSelectionMatcher(spec)

      val filter =
         resolveClassMethodNameFilterClass()
            .declaredConstructors.first { it.parameterCount == 1 }.let {
               it.isAccessible = true
               it.newInstance(matcher)
            }
      ClassMethodNameFilterUtils.extractIncludePatterns(listOf(filter)) shouldBe listOf(
         "\\QClassA\\E",
         "\\QClassB.test name\\E"
      )
   }

   test("reset clears buildScript include patterns as well as command-line patterns") {

      // first arg → buildScriptIncludePatterns; third arg → commandLineIncludePatterns
      val spec = TestFilterSpec(
         setOf("BuildScriptInclude"),
         emptySet(),
         setOf("CommandLineInclude"),
      )

      val matcher = TestSelectionMatcher(spec)

      val filter =
         resolveClassMethodNameFilterClass()
            .declaredConstructors.first { it.parameterCount == 1 }.let {
               it.isAccessible = true
               it.newInstance(matcher)
            }

      ClassMethodNameFilterUtils.extractIncludePatterns(listOf(filter)).shouldBe(
         listOf("\\QBuildScriptInclude\\E", "\\QCommandLineInclude\\E")
      )

      ClassMethodNameFilterUtils.reset(listOf(filter))

      ClassMethodNameFilterUtils.extractIncludePatterns(listOf(filter)).shouldBeEmpty()
   }

})
