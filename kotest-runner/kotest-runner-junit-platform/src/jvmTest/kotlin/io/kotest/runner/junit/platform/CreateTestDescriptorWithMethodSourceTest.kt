package io.kotest.runner.junit.platform

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.test.names.DisplayNameFormatting
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.MethodSource

/**
 * Tests for [createTestDescriptorWithMethodSource] to ensure that:
 * - CONTAINER type tests use [ClassSource] for proper tree rendering in Android Studio
 * - TEST (leaf) type tests use [MethodSource] for Maven surefire-junit-platform compatibility
 *
 * This is needed because:
 * - Android Studio's ijLog parser does not correctly render nested test trees when CONTAINER
 *   tests use MethodSource - sibling containers appear incorrectly nested under each other
 * - Maven surefire-junit-platform needs MethodSource for leaf tests to produce correct XML reports
 *
 */
class CreateTestDescriptorWithMethodSourceTest : FunSpec({

   val root = EngineDescriptorBuilder
      .builder(UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID))
      .withSpecs(listOf(SpecRef.Reference(DummySpec::class, DummySpec::class.java.name)))
      .build()

   val containerTestCase = TestCase(
      DummySpec::class.toDescriptor().append("container test"),
      TestNameBuilder.builder("container test").build(),
      DummySpec(),
      { 1 + 1 shouldBe 2 },
      SourceRef.None,
      TestType.Container,
   )

   val leafTestCase = TestCase(
      containerTestCase.descriptor.append("leaf test"),
      TestNameBuilder.builder("leaf test").build(),
      containerTestCase.spec,
      { 1 + 1 shouldBe 2 },
      SourceRef.None,
      TestType.Test,
      parent = containerTestCase,
   )

   test("CONTAINER type tests should use ClassSource for Android Studio compatibility") {
      val descriptor = createTestDescriptorWithMethodSource(
         root = root,
         testCase = containerTestCase,
         type = TestDescriptor.Type.CONTAINER,
         formatter = DisplayNameFormatting(null),
      )

      descriptor.source.isPresent shouldBe true
      descriptor.source.get().shouldBeInstanceOf<ClassSource>()

      val classSource = descriptor.source.get() as ClassSource
      classSource.className shouldBe DummySpec::class.qualifiedName
   }

   test("TEST type tests should use MethodSource for Maven surefire compatibility") {
      val descriptor = createTestDescriptorWithMethodSource(
         root = root,
         testCase = leafTestCase,
         type = TestDescriptor.Type.TEST,
         formatter = DisplayNameFormatting(null),
      )

      descriptor.source.isPresent shouldBe true
      descriptor.source.get().shouldBeInstanceOf<MethodSource>()

      val methodSource = descriptor.source.get() as MethodSource
      methodSource.className shouldBe DummySpec::class.qualifiedName
      methodSource.methodName shouldBe "container test/leaf test"
   }

   test("CONTAINER_AND_TEST type tests should use MethodSource") {
      val descriptor = createTestDescriptorWithMethodSource(
         root = root,
         testCase = containerTestCase,
         type = TestDescriptor.Type.CONTAINER_AND_TEST,
         formatter = DisplayNameFormatting(null),
      )

      descriptor.source.isPresent shouldBe true
      descriptor.source.get().shouldBeInstanceOf<MethodSource>()

      val methodSource = descriptor.source.get() as MethodSource
      methodSource.className shouldBe DummySpec::class.qualifiedName
      methodSource.methodName shouldBe "container test"
   }

   // The IntelliJ plugin parses `proxy.locationUrl = "java:test://<fqn>/<seg>/<seg>"` to drive
   // jump-to-source. That URL is built by IntelliJ's JUnit5 launcher from this MethodSource, so
   // the methodName MUST join nested-test path segments with a single `/` (and never with the
   // legacy ` -- ` separator). The tests below pin that contract.
   context("nested tests use '/' as the path separator in methodName") {

      test("two levels - methodName is 'outer/leaf'") {
         val descriptor = createTestDescriptorWithMethodSource(
            root = root,
            testCase = leafTestCase,
            type = TestDescriptor.Type.TEST,
            formatter = DisplayNameFormatting(null),
         )

         val methodSource = descriptor.source.get() as MethodSource
         methodSource.className shouldBe DummySpec::class.qualifiedName
         methodSource.methodName shouldBe "container test/leaf test"
      }

      test("three levels - methodName is 'outer/middle/leaf'") {
         val middle = TestCase(
            containerTestCase.descriptor.append("middle context"),
            TestNameBuilder.builder("middle context").build(),
            containerTestCase.spec,
            { },
            SourceRef.None,
            TestType.Container,
            parent = containerTestCase,
         )
         val deepLeaf = TestCase(
            middle.descriptor.append("deep leaf"),
            TestNameBuilder.builder("deep leaf").build(),
            containerTestCase.spec,
            { },
            SourceRef.None,
            TestType.Test,
            parent = middle,
         )

         val descriptor = createTestDescriptorWithMethodSource(
            root = root,
            testCase = deepLeaf,
            type = TestDescriptor.Type.TEST,
            formatter = DisplayNameFormatting(null),
         )

         val methodSource = descriptor.source.get() as MethodSource
         methodSource.methodName shouldBe "container test/middle context/deep leaf"
      }

      test("four levels deep - methodName joins all segments with '/'") {
         val l2 = TestCase(
            containerTestCase.descriptor.append("l2"),
            TestNameBuilder.builder("l2").build(),
            containerTestCase.spec,
            { },
            SourceRef.None,
            TestType.Container,
            parent = containerTestCase,
         )
         val l3 = TestCase(
            l2.descriptor.append("l3"),
            TestNameBuilder.builder("l3").build(),
            containerTestCase.spec,
            { },
            SourceRef.None,
            TestType.Container,
            parent = l2,
         )
         val l4 = TestCase(
            l3.descriptor.append("l4 leaf"),
            TestNameBuilder.builder("l4 leaf").build(),
            containerTestCase.spec,
            { },
            SourceRef.None,
            TestType.Test,
            parent = l3,
         )

         val descriptor = createTestDescriptorWithMethodSource(
            root = root,
            testCase = l4,
            type = TestDescriptor.Type.TEST,
            formatter = DisplayNameFormatting(null),
         )

         val methodSource = descriptor.source.get() as MethodSource
         methodSource.methodName shouldBe "container test/l2/l3/l4 leaf"
      }

      test("test names with spaces and punctuation are preserved verbatim around the '/'") {
         val parent = TestCase(
            DummySpec::class.toDescriptor().append("a context with spaces"),
            TestNameBuilder.builder("a context with spaces").build(),
            DummySpec(),
            { },
            SourceRef.None,
            TestType.Container,
         )
         val child = TestCase(
            parent.descriptor.append("when X happens, then Y"),
            TestNameBuilder.builder("when X happens, then Y").build(),
            parent.spec,
            { },
            SourceRef.None,
            TestType.Test,
            parent = parent,
         )

         val descriptor = createTestDescriptorWithMethodSource(
            root = root,
            testCase = child,
            type = TestDescriptor.Type.TEST,
            formatter = DisplayNameFormatting(null),
         )

         val methodSource = descriptor.source.get() as MethodSource
         methodSource.methodName shouldBe "a context with spaces/when X happens, then Y"
         // sanity: no legacy ` -- ` separator leaked into the methodName
         methodSource.methodName shouldNotContain " -- "
      }

      test("nested CONTAINER descriptors still use ClassSource (not MethodSource)") {
         // CONTAINER descriptors use ClassSource for Android Studio rendering. The MethodSource
         // path encoding only applies to leaves and CONTAINER_AND_TEST nodes. This test pins that
         // distinction so a future refactor doesn't accidentally start writing slash-paths into
         // CONTAINER descriptors and confuse the IDE tree.
         val middle = TestCase(
            containerTestCase.descriptor.append("middle"),
            TestNameBuilder.builder("middle").build(),
            containerTestCase.spec,
            { },
            SourceRef.None,
            TestType.Container,
            parent = containerTestCase,
         )

         val descriptor = createTestDescriptorWithMethodSource(
            root = root,
            testCase = middle,
            type = TestDescriptor.Type.CONTAINER,
            formatter = DisplayNameFormatting(null),
         )

         descriptor.source.get().shouldBeInstanceOf<ClassSource>()
      }
   }

   context("display name truncation") {

      val longNameTestCase = TestCase(
         DummySpec::class.toDescriptor().append("a".repeat(MAX_TRUNCATED_NAME_LENGTH + 10)),
         TestNameBuilder.builder("a".repeat(MAX_TRUNCATED_NAME_LENGTH + 10)).build(),
         DummySpec(),
         { },
         SourceRef.None,
         TestType.Container,
      )

      test("CONTAINER display name is not truncated when env var is not set") {
         val descriptor = createTestDescriptorWithMethodSource(
            root = root,
            testCase = longNameTestCase,
            type = TestDescriptor.Type.CONTAINER,
            formatter = DisplayNameFormatting(null),
         )
         descriptor.displayName shouldBe "a".repeat(MAX_TRUNCATED_NAME_LENGTH + 10)
      }

      test("CONTAINER display name is truncated when env var is set") {
         withSystemProperty(TRUNCATE_TEST_NAMES_ENV, "true") {
            val descriptor = createTestDescriptorWithMethodSource(
               root = root,
               testCase = longNameTestCase,
               type = TestDescriptor.Type.CONTAINER,
               formatter = DisplayNameFormatting(null),
            )
            descriptor.displayName shouldBe "a".repeat(MAX_TRUNCATED_NAME_LENGTH - 3) + "..."
         }
      }

      test("TEST (leaf) display name is never truncated even when env var is set") {
         val longLeafTestCase = TestCase(
            longNameTestCase.descriptor.append("leaf"),
            TestNameBuilder.builder("a".repeat(MAX_TRUNCATED_NAME_LENGTH + 10)).build(),
            longNameTestCase.spec,
            { },
            SourceRef.None,
            TestType.Test,
            parent = longNameTestCase,
         )
         withSystemProperty(TRUNCATE_TEST_NAMES_ENV, "true") {
            val descriptor = createTestDescriptorWithMethodSource(
               root = root,
               testCase = longLeafTestCase,
               type = TestDescriptor.Type.TEST,
               formatter = DisplayNameFormatting(null),
            )
            descriptor.displayName shouldBe "a".repeat(MAX_TRUNCATED_NAME_LENGTH + 10)
         }
      }

      test("short CONTAINER display name is not truncated even when env var is set") {
         withSystemProperty(TRUNCATE_TEST_NAMES_ENV, "true") {
            val descriptor = createTestDescriptorWithMethodSource(
               root = root,
               testCase = containerTestCase,
               type = TestDescriptor.Type.CONTAINER,
               formatter = DisplayNameFormatting(null),
            )
            descriptor.displayName shouldBe "container test"
         }
      }
   }
})

private class DummySpec : FunSpec({})
