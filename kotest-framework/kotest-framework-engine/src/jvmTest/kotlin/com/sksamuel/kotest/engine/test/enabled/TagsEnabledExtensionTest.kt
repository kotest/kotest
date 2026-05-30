package com.sksamuel.kotest.engine.test.enabled

import io.kotest.core.Tag
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.config.TestConfig
import io.kotest.core.test.TestType
import io.kotest.core.descriptors.toDescriptor
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.tags.TagExpression
import io.kotest.engine.test.enabled.TagsEnabledExtension
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
@Isolate
class TagsEnabledExtensionTest : FunSpec({

   val spec = TagsEnabledExtensionSpec()

   val testCase = TestCase(
      descriptor = TagsEnabledExtensionTest::class.toDescriptor().append("test"),
      name = TestNameBuilder.builder("test").build(),
      spec = spec,
      test = { },
      source = SourceRef.None,
      type = TestType.Test,
   )

   fun makeContainer(name: String, parent: TestCase? = null) = TestCase(
      descriptor = (parent?.descriptor ?: TagsEnabledExtensionSpec::class.toDescriptor()).append(name),
      name = TestNameBuilder.builder(name).build(),
      spec = spec,
      test = { },
      source = SourceRef.None,
      type = TestType.Container,
      parent = parent,
   )

   test("TagsEnabledExtension should disable test when excluded by tag expression") {
      // Expression requires tag "included" but test has no tags, so it should be excluded
      val tags = TagExpression("included")
      val resolver = TestConfigResolver()
      TagsEnabledExtension(tags, resolver).isEnabled(testCase).isDisabled shouldBe true
   }

   test("TagsEnabledExtension should return enabled when KOTEST_TEST_ENABLED_OVERRIDE is set") {
      try {
         System.setProperty(KotestEngineProperties.KOTEST_TEST_ENABLED_OVERRIDE, "true")
         val tags = TagExpression("included")
         val resolver = TestConfigResolver()
         TagsEnabledExtension(tags, resolver).isEnabled(testCase) shouldBe Enabled.enabled
      } finally {
         System.getProperties().remove(KotestEngineProperties.KOTEST_TEST_ENABLED_OVERRIDE)
      }
   }

   test("TagsEnabledExtension should enable container when its path equals KOTEST_DATA_TEST_ANCESTOR_PATH") {
      try {
         System.setProperty(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH, "parent context")
         val resolver = TestConfigResolver()
         val container = makeContainer("parent context")
         TagsEnabledExtension(TagExpression("kotest.data.999"), resolver).isEnabled(container) shouldBe Enabled.enabled
      } finally {
         System.getProperties().remove(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH)
      }
   }

   test("TagsEnabledExtension should enable ancestor container when its path is a prefix of KOTEST_DATA_TEST_ANCESTOR_PATH") {
      try {
         System.setProperty(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH, "parent context -- child context")
         val resolver = TestConfigResolver()
         val parentContainer = makeContainer("parent context")
         TagsEnabledExtension(TagExpression("kotest.data.999"), resolver).isEnabled(parentContainer) shouldBe Enabled.enabled
      } finally {
         System.getProperties().remove(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH)
      }
   }

   test("TagsEnabledExtension should enable deepest ancestor container when its path equals KOTEST_DATA_TEST_ANCESTOR_PATH") {
      try {
         System.setProperty(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH, "parent context -- child context")
         val resolver = TestConfigResolver()
         val parentContainer = makeContainer("parent context")
         val childContainer = makeContainer("child context", parent = parentContainer)
         TagsEnabledExtension(TagExpression("kotest.data.999"), resolver).isEnabled(childContainer) shouldBe Enabled.enabled
      } finally {
         System.getProperties().remove(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH)
      }
   }

   test("TagsEnabledExtension should not bypass a sibling container whose path does not match KOTEST_DATA_TEST_ANCESTOR_PATH") {
      try {
         System.setProperty(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH, "parent context -- child context")
         val resolver = TestConfigResolver()
         val parentContainer = makeContainer("parent context")
         val siblingContainer = makeContainer("sibling", parent = parentContainer)
         TagsEnabledExtension(TagExpression("kotest.data.999"), resolver).isEnabled(siblingContainer).isDisabled shouldBe true
      } finally {
         System.getProperties().remove(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH)
      }
   }

   test("TagsEnabledExtension should not bypass a leaf test even when KOTEST_DATA_TEST_ANCESTOR_PATH matches its name") {
      try {
         System.setProperty(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH, "test")
         val resolver = TestConfigResolver()
         TagsEnabledExtension(TagExpression("kotest.data.999"), resolver).isEnabled(testCase).isDisabled shouldBe true
      } finally {
         System.getProperties().remove(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH)
      }
   }

   test("TagsEnabledExtension should not bypass a container that has explicit tags even when its path matches") {
      try {
         System.setProperty(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH, "parent context")
         val resolver = TestConfigResolver()
         val taggedContainer = TestCase(
            descriptor = TagsEnabledExtensionSpec::class.toDescriptor().append("parent context"),
            name = TestNameBuilder.builder("parent context").build(),
            spec = spec,
            test = { },
            source = SourceRef.None,
            type = TestType.Container,
            config = TestConfig(tags = setOf(SomeContainerTag)),
         )
         TagsEnabledExtension(TagExpression("kotest.data.999"), resolver).isEnabled(taggedContainer).isDisabled shouldBe true
      } finally {
         System.getProperties().remove(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH)
      }
   }

   test("TagsEnabledExtension should not bypass a container when KOTEST_DATA_TEST_ANCESTOR_PATH is not set") {
      val resolver = TestConfigResolver()
      val container = makeContainer("parent context")
      TagsEnabledExtension(TagExpression("kotest.data.999"), resolver).isEnabled(container).isDisabled shouldBe true
   }
})

private object SomeContainerTag : Tag()

private class TagsEnabledExtensionSpec : FunSpec()
