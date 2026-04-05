package io.kotest.runner.junit.platform.gradle

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.filter.DescriptorFilterResult
import io.kotest.engine.extensions.filter.INCLUDE_PATTERN_ENV
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class ClassMethodNameFilterAdapterTest : FunSpec({

   // ---------------------------------------------------------------------------
   // parseNestedTestPattern — pattern parsing
   // ---------------------------------------------------------------------------

   context("parseNestedTestPattern returns null for non-nested patterns") {

      test("package only") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern("\\Qio.kotest\\E") shouldBe null
      }

      test("class only") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern("\\Qio.kotest.SomeTest\\E") shouldBe null
      }

      test("root test (no context separator)") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern("\\Qio.kotest.SomeTest.my test\\E") shouldBe null
      }

      test("leading wildcard class pattern") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern(".*\\QFooTest\\E") shouldBe null
      }

      test("surrounding wildcard pattern") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern(".*\\QFooTest\\E.*") shouldBe null
      }

      test("wildcard only") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern(".*") shouldBe null
      }

      test("no leading \\Q but still a root test") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern("io.kotest.SomeTest.my test") shouldBe null
      }
   }

   context("parseNestedTestPattern returns NestedTestPattern for nested patterns") {

      test("simple two-level nested test") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern("\\Qio.kotest.SomeTest.test -- nested\\E") shouldBe
            NestedTestPattern("io.kotest.SomeTest", listOf("test", "nested"))
      }

      test("three-level nesting") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern("\\Qio.kotest.SomeTest.ctx -- level2 -- leaf\\E") shouldBe
            NestedTestPattern("io.kotest.SomeTest", listOf("ctx", "level2", "leaf"))
      }

      test("spaces in test names") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern("\\Qio.kotest.SomeTest.some method name -- more names\\E") shouldBe
            NestedTestPattern("io.kotest.SomeTest", listOf("some method name", "more names"))
      }

      test("wildcard in nested context name") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern(
            "\\Qcom.sksamuel.kotest.engine.gradle.Test.some method name -- with a \\E.*\\Q wildcard\\E"
         ) shouldBe NestedTestPattern(
            "com.sksamuel.kotest.engine.gradle.Test",
            listOf("some method name", "with a * wildcard")
         )
      }

      test("wildcard in first context segment") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern(
            "\\Qio.kotest.SomeTest.ctx 1\\E.*\\Q2 -- nested\\E"
         ) shouldBe NestedTestPattern("io.kotest.SomeTest", listOf("ctx 1*2", "nested"))
      }

      test("no leading \\Q") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern("io.kotest.SomeTest.test -- nested\\E") shouldBe
            NestedTestPattern("io.kotest.SomeTest", listOf("test", "nested"))
      }

      test("no trailing \\E") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern("\\Qio.kotest.SomeTest.test -- nested") shouldBe
            NestedTestPattern("io.kotest.SomeTest", listOf("test", "nested"))
      }

      test("deep package with single-segment class") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern(
            "\\Qcom.sksamuel.kotest.runner.junit.platform.gradle.MySpec.a -- b\\E"
         ) shouldBe NestedTestPattern(
            "com.sksamuel.kotest.runner.junit.platform.gradle.MySpec",
            listOf("a", "b")
         )
      }

      test("class with no package") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern("\\QMySpec.root -- child\\E") shouldBe
            NestedTestPattern("MySpec", listOf("root", "child"))
      }

      test("multiple wildcards across both context levels") {
         ClassMethodNameFilterAdapter.parseNestedTestPattern(
            "\\Qio.example.Spec.ctx 1\\E.*\\Q2 -- leaf 3\\E.*\\Q4\\E"
         ) shouldBe NestedTestPattern("io.example.Spec", listOf("ctx 1*2", "leaf 3*4"))
      }
   }

   // ---------------------------------------------------------------------------
   // NestedTestDescriptorFilter — descriptor matching
   // ---------------------------------------------------------------------------

   context("NestedTestDescriptorFilter") {

      val fqcn = ClassMethodNameFilterAdapterTest::class.qualifiedName!!
      val spec: Descriptor.SpecDescriptor = ClassMethodNameFilterAdapterTest::class.toDescriptor()

      context("spec is included when it lies on the path to the target") {
         test("spec matches when the target is inside it") {
            val pattern = NestedTestPattern(fqcn, listOf("a", "b"))
            NestedTestDescriptorFilter(setOf(pattern)).filter(spec) shouldBe DescriptorFilterResult.Include
         }

         test("spec from a different package is excluded") {
            val pattern = NestedTestPattern("io.kotest.runner.junit.platform.xxx.ClassMethodNameFilterAdapterTest", listOf("a", "b"))
            NestedTestDescriptorFilter(setOf(pattern)).filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
         }

         test("spec with a different class name is excluded") {
            val pattern = NestedTestPattern("io.kotest.runner.junit.platform.gradle.NotThisClass", listOf("a", "b"))
            NestedTestDescriptorFilter(setOf(pattern)).filter(spec) shouldBe DescriptorFilterResult.Exclude(null)
         }
      }

      context("nested tests are included or excluded based on their path") {
         val container = spec.append("a")
         val target = container.append("b")
         val sibling = spec.append("c")
         val cousin = container.append("d")

         val pattern = NestedTestPattern(fqcn, listOf("a", "b"))
         val filter = NestedTestDescriptorFilter(setOf(pattern))

         test("direct parent container is included") {
            filter.filter(container) shouldBe DescriptorFilterResult.Include
         }

         test("exact target is included") {
            filter.filter(target) shouldBe DescriptorFilterResult.Include
         }

         test("sibling of the parent container is excluded") {
            filter.filter(sibling) shouldBe DescriptorFilterResult.Exclude(null)
         }

         test("sibling of the target under the same container is excluded") {
            filter.filter(cousin) shouldBe DescriptorFilterResult.Exclude(null)
         }
      }

      context("descendants of the target are included") {
         val container = spec.append("a")
         val target = container.append("b")
         val grandChild = target.append("leaf")
         val deepLeaf = grandChild.append("deeper")

         val pattern = NestedTestPattern(fqcn, listOf("a", "b"))
         val filter = NestedTestDescriptorFilter(setOf(pattern))

         test("child of target is included") {
            filter.filter(grandChild) shouldBe DescriptorFilterResult.Include
         }

         test("grandchild of target is included") {
            filter.filter(deepLeaf) shouldBe DescriptorFilterResult.Include
         }
      }

      context("three-level nesting") {
         val root = spec.append("root")
         val mid = root.append("mid")
         val leaf = mid.append("leaf")
         val unrelatedRoot = spec.append("other")
         val unrelatedMid = unrelatedRoot.append("mid")

         val pattern = NestedTestPattern(fqcn, listOf("root", "mid", "leaf"))
         val filter = NestedTestDescriptorFilter(setOf(pattern))

         test("spec is included") {
            filter.filter(spec) shouldBe DescriptorFilterResult.Include
         }

         test("root container is included") {
            filter.filter(root) shouldBe DescriptorFilterResult.Include
         }

         test("mid container is included") {
            filter.filter(mid) shouldBe DescriptorFilterResult.Include
         }

         test("exact leaf is included") {
            filter.filter(leaf) shouldBe DescriptorFilterResult.Include
         }

         test("unrelated root context is excluded") {
            filter.filter(unrelatedRoot) shouldBe DescriptorFilterResult.Exclude(null)
         }

         test("same-named mid under different root is excluded") {
            filter.filter(unrelatedMid) shouldBe DescriptorFilterResult.Exclude(null)
         }
      }

      context("multiple patterns — any match includes the descriptor") {
         val patternA = NestedTestPattern(fqcn, listOf("ctx-a", "test-a"))
         val patternB = NestedTestPattern(fqcn, listOf("ctx-b", "test-b"))
         val filter = NestedTestDescriptorFilter(setOf(patternA, patternB))

         test("descriptor matching first pattern is included") {
            filter.filter(spec.append("ctx-a").append("test-a")) shouldBe DescriptorFilterResult.Include
         }

         test("descriptor matching second pattern is included") {
            filter.filter(spec.append("ctx-b").append("test-b")) shouldBe DescriptorFilterResult.Include
         }

         test("descriptor matching neither pattern is excluded") {
            filter.filter(spec.append("ctx-c").append("test-c")) shouldBe DescriptorFilterResult.Exclude(null)
         }
      }

      context("INCLUDE_PATTERN_ENV overrides the filter") {
         val pattern = NestedTestPattern("io.nothing.NotThisSpec", listOf("x", "y"))
         val filter = NestedTestDescriptorFilter(setOf(pattern))
         val unrelatedDescriptor = spec.append("something").append("else")

         test("any descriptor is included when INCLUDE_PATTERN_ENV is set") {
            withEnvironment(INCLUDE_PATTERN_ENV, "anything") {
               filter.filter(unrelatedDescriptor) shouldBe DescriptorFilterResult.Include
            }
         }
      }

      context("empty pattern set") {
         test("any descriptor is included when no patterns are provided") {
            val filter = NestedTestDescriptorFilter(emptySet())
            filter.filter(spec) shouldBe DescriptorFilterResult.Include
            filter.filter(spec.append("a").append("b")) shouldBe DescriptorFilterResult.Include
         }
      }
   }

   // ---------------------------------------------------------------------------
   // parseNestedTestPattern + NestedTestDescriptorFilter round-trip
   // ---------------------------------------------------------------------------

   context("round-trip: parse a Gradle pattern and filter descriptors") {

      val spec = Descriptor.SpecDescriptor(DescriptorId("io.kotest.runner.junit.platform.gradle.ClassMethodNameFilterAdapterTest"))

      test("two-level nested pattern matches the correct descriptor") {
         val pattern = ClassMethodNameFilterAdapter.parseNestedTestPattern(
            "\\Qio.kotest.runner.junit.platform.gradle.ClassMethodNameFilterAdapterTest.a -- b\\E"
         )
         pattern shouldNotBe null
         val filter = NestedTestDescriptorFilter(setOf(pattern!!))

         filter.filter(spec) shouldBe DescriptorFilterResult.Include
         filter.filter(spec.append("a")) shouldBe DescriptorFilterResult.Include
         filter.filter(spec.append("a").append("b")) shouldBe DescriptorFilterResult.Include
         filter.filter(spec.append("a").append("b").append("child")) shouldBe DescriptorFilterResult.Include
         filter.filter(spec.append("a").append("c")) shouldBe DescriptorFilterResult.Exclude(null)
         filter.filter(spec.append("x")) shouldBe DescriptorFilterResult.Exclude(null)
      }

      test("three-level nested pattern matches the correct descriptor") {
         val pattern = ClassMethodNameFilterAdapter.parseNestedTestPattern(
            "\\Qio.kotest.runner.junit.platform.gradle.ClassMethodNameFilterAdapterTest.ctx -- mid -- leaf\\E"
         )
         pattern shouldNotBe null
         val filter = NestedTestDescriptorFilter(setOf(pattern!!))

         filter.filter(spec.append("ctx").append("mid").append("leaf")) shouldBe DescriptorFilterResult.Include
         filter.filter(spec.append("ctx").append("mid")) shouldBe DescriptorFilterResult.Include
         filter.filter(spec.append("ctx")) shouldBe DescriptorFilterResult.Include
         filter.filter(spec.append("ctx").append("other")) shouldBe DescriptorFilterResult.Exclude(null)
      }

      test("wildcard in method name is preserved after parsing") {
         val pattern = ClassMethodNameFilterAdapter.parseNestedTestPattern(
            "\\Qcom.sksamuel.kotest.engine.gradle.Test.some method name -- with a \\E.*\\Q wildcard\\E"
         )
         pattern shouldNotBe null
         pattern!!.fqcn shouldBe "com.sksamuel.kotest.engine.gradle.Test"
         pattern.contexts shouldBe listOf("some method name", "with a * wildcard")
      }
   }
})
