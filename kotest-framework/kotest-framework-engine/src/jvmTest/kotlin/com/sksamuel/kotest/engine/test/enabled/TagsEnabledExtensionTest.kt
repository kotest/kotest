package com.sksamuel.kotest.engine.test.enabled

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
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
})

private class TagsEnabledExtensionSpec : FunSpec()
