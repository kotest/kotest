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
