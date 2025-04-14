package com.sksamuel.kotest.engine.test.status

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.status.SystemPropertyTestFilterEnabledExtension
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class SystemPropertyTestFilterEnabledExtensionTest : FunSpec() {
   init {
      test("should include tests when no filter system property or environment variable is specified") {
         val tc = TestCase(
            SystemPropertyTestFilterEnabledExtensionTest::class.toDescriptor().append("foo"),
            TestNameBuilder.builder("foo").build(),
            SystemPropertyTestFilterEnabledExtensionTest(),
            {},
            SourceRef.None,
            TestType.Test
         )

         SystemPropertyTestFilterEnabledExtension.isEnabled(tc).shouldBe(Enabled.enabled)
      }

      xtest("should include tests that match pattern in system property") {
         val tc = TestCase(
            SystemPropertyTestFilterEnabledExtensionTest::class.toDescriptor().append("foo"),
            TestNameBuilder.builder("foo").build(),
            SystemPropertyTestFilterEnabledExtensionTest(),
            {},
            SourceRef.None,
            TestType.Test
         )

         withSystemProperty(KotestEngineProperties.filterTests, "foo") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc).shouldBe(Enabled.enabled)
         }

         withSystemProperty(KotestEngineProperties.filterTests, "f*") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc).shouldBe(Enabled.enabled)
         }

         withSystemProperty(KotestEngineProperties.filterTests, "*o") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc).shouldBe(Enabled.enabled)
         }
      }

      test("should exclude tests that do not match pattern in system property") {
         val tc = TestCase(
            SystemPropertyTestFilterEnabledExtensionTest::class.toDescriptor().append("foo"),
            TestNameBuilder.builder("foo").build(),
            SystemPropertyTestFilterEnabledExtensionTest(),
            {},
            SourceRef.None,
            TestType.Test
         )

         withSystemProperty(KotestEngineProperties.filterTests, "goo") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc)
               .shouldBe(Enabled.disabled("Excluded by 'kotest.filter.tests': goo"))
         }

         withSystemProperty(KotestEngineProperties.filterTests, "g*") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc)
               .shouldBe(Enabled.disabled("Excluded by 'kotest.filter.tests': g.*?"))
         }

         withSystemProperty(KotestEngineProperties.filterTests, "*p") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc)
               .shouldBe(Enabled.disabled("Excluded by 'kotest.filter.tests': .*?p"))
         }
      }
   }
}
