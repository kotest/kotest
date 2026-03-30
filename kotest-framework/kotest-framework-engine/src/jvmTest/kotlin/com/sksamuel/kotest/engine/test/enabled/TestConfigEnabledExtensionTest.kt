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
import io.kotest.core.test.config.TestConfig
import io.kotest.core.descriptors.toDescriptor
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.enabled.TestConfigEnabledExtension
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
@Isolate
class TestConfigEnabledExtensionTest : FunSpec({

   val spec = TestConfigEnabledExtensionSpec()

   val disabledTestCase = TestCase(
      descriptor = TestConfigEnabledExtensionTest::class.toDescriptor().append("disabled"),
      name = TestNameBuilder.builder("disabled").build(),
      spec = spec,
      test = { },
      source = SourceRef.None,
      type = TestType.Test,
      config = TestConfig(enabled = false),
   )

   test("TestConfigEnabledExtension should return disabled when test config has enabled = false") {
      val resolver = TestConfigResolver()
      TestConfigEnabledExtension(resolver).isEnabled(disabledTestCase).isDisabled shouldBe true
   }

   test("TestConfigEnabledExtension should return enabled when KOTEST_TEST_ENABLED_OVERRIDE is set") {
      try {
         System.setProperty(KotestEngineProperties.KOTEST_TEST_ENABLED_OVERRIDE, "true")
         val resolver = TestConfigResolver()
         TestConfigEnabledExtension(resolver).isEnabled(disabledTestCase) shouldBe Enabled.enabled
      } finally {
         System.getProperties().remove(KotestEngineProperties.KOTEST_TEST_ENABLED_OVERRIDE)
      }
   }
})

private class TestConfigEnabledExtensionSpec : FunSpec()
