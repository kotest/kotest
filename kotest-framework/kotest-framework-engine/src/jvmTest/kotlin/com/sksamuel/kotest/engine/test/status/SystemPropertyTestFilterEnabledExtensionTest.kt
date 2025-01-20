package com.sksamuel.kotest.engine.test.status

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.descriptors.append
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.status.SystemPropertyTestFilterEnabledExtension
import io.kotest.extensions.system.withEnvironment
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
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

      test("should include tests that match pattern in system property") {
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
               .shouldBe(Enabled.disabled("Excluded by kotest.filter.tests test filter: goo"))
         }

         withSystemProperty(KotestEngineProperties.filterTests, "g*") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc)
               .shouldBe(Enabled.disabled("Excluded by kotest.filter.tests test filter: g.*?"))
         }

         withSystemProperty(KotestEngineProperties.filterTests, "*p") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc)
               .shouldBe(Enabled.disabled("Excluded by kotest.filter.tests test filter: .*?p"))
         }
      }

      test("should include tests that match pattern in environment variable") {
         val tc = TestCase(
            SystemPropertyTestFilterEnabledExtensionTest::class.toDescriptor().append("foo"),
            TestNameBuilder.builder("foo").build(),
            SystemPropertyTestFilterEnabledExtensionTest(),
            {},
            SourceRef.None,
            TestType.Test
         )

         withEnvironment(KotestEngineProperties.filterTests, "foo") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc).shouldBe(Enabled.enabled)
         }

         withEnvironment(KotestEngineProperties.filterTests, "f*") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc).shouldBe(Enabled.enabled)
         }

         withEnvironment(KotestEngineProperties.filterTests, "*o") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc).shouldBe(Enabled.enabled)
         }
      }

      test("should exclude tests that do not match pattern in environment variable") {
         val tc = TestCase(
            SystemPropertyTestFilterEnabledExtensionTest::class.toDescriptor().append("foo"),
            TestNameBuilder.builder("foo").build(),
            SystemPropertyTestFilterEnabledExtensionTest(),
            {},
            SourceRef.None,
            TestType.Test
         )

         withEnvironment(KotestEngineProperties.filterTests, "goo") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc)
               .shouldBe(Enabled.disabled("Excluded by kotest.filter.tests test filter: goo"))
         }

         withEnvironment(KotestEngineProperties.filterTests, "g*") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc)
               .shouldBe(Enabled.disabled("Excluded by kotest.filter.tests test filter: g.*?"))
         }

         withEnvironment(KotestEngineProperties.filterTests, "*p") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc)
               .shouldBe(Enabled.disabled("Excluded by kotest.filter.tests test filter: .*?p"))
         }
      }

      test("should include tests that match pattern in environment variable with underscores") {
         val tc = TestCase(
            SystemPropertyTestFilterEnabledExtensionTest::class.toDescriptor().append("foo"),
            TestNameBuilder.builder("foo").build(),
            SystemPropertyTestFilterEnabledExtensionTest(),
            {},
            SourceRef.None,
            TestType.Test
         )

         withEnvironment(KotestEngineProperties.filterTests.replace('.', '_'), "foo") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc).shouldBe(Enabled.enabled)
         }

         withEnvironment(KotestEngineProperties.filterTests.replace('.', '_'), "f*") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc).shouldBe(Enabled.enabled)
         }

         withEnvironment(KotestEngineProperties.filterTests.replace('.', '_'), "*o") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc).shouldBe(Enabled.enabled)
         }
      }

      test("should exclude tests that do not match pattern in environment variable with underscores") {
         val tc = TestCase(
            SystemPropertyTestFilterEnabledExtensionTest::class.toDescriptor().append("foo"),
            TestNameBuilder.builder("foo").build(),
            SystemPropertyTestFilterEnabledExtensionTest(),
            {},
            SourceRef.None,
            TestType.Test
         )

         withEnvironment(KotestEngineProperties.filterTests.replace('.', '_'), "goo") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc)
               .shouldBe(Enabled.disabled("Excluded by kotest.filter.tests test filter: goo"))
         }

         withEnvironment(KotestEngineProperties.filterTests.replace('.', '_'), "g*") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc)
               .shouldBe(Enabled.disabled("Excluded by kotest.filter.tests test filter: g.*?"))
         }

         withEnvironment(KotestEngineProperties.filterTests.replace('.', '_'), "*p") {
            SystemPropertyTestFilterEnabledExtension.isEnabled(tc)
               .shouldBe(Enabled.disabled("Excluded by kotest.filter.tests test filter: .*?p"))
         }
      }
   }
}
