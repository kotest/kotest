package com.sksamuel.kotest.engine.active

import io.kotest.common.ExperimentalKotest
import io.kotest.core.NamedTag
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.extensions.TagExtension
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.core.descriptors.toDescriptor
import io.kotest.engine.extensions.DescriptorFilter
import io.kotest.engine.extensions.DescriptorFilterResult
import io.kotest.engine.tags.TagExpression
import io.kotest.engine.test.status.isEnabled
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.matchers.shouldBe

@ExperimentalKotest
class IsEnabledTest : StringSpec() {

   init {

      "isEnabledInternal should return false if the test is disabled in config" {
         val test = TestCase(
            name = TestNameBuilder.builder("foo").build(),
            descriptor = IsEnabledTest::class.toDescriptor().append("foo"),
            spec = this@IsEnabledTest,
            parent = null,
            test = {},
            config = TestConfig(enabledOrReasonIf = { Enabled.disabled }),
            type = TestType.Test,
         )
         test.isEnabledInternal(
            ProjectConfigResolver(),
            TestConfigResolver()
         ).isEnabled shouldBe false
      }

      "isEnabledInternal should return false if it has an excluded tag" {

         val mytag = NamedTag("mytag")

         val ext = TagExtension { TagExpression(emptySet(), setOf(mytag)) }

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ext)
         }

         val test = TestCase(
            name = TestNameBuilder.builder("foo").build(),
            descriptor = IsEnabledTest::class.toDescriptor().append("foo"),
            spec = this@IsEnabledTest,
            parent = null,
            test = {},
            config = TestConfig(tags = setOf(mytag)),
            type = TestType.Test,
         )

         test.isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled shouldBe false
      }

      "isEnabledInternal should return false if it is excluded by a tag expression" {
         val mytag = NamedTag("mytag")

         val ext = TagExtension { TagExpression("!mytag") }

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ext)
         }

         val test = TestCase(
            name = TestNameBuilder.builder("foo").build(),
            descriptor = IsEnabledTest::class.toDescriptor().append("foo"),
            spec = this@IsEnabledTest,
            parent = null,
            test = {},
            config = TestConfig(tags = setOf(mytag)),
            type = TestType.Test,
         )

         test.isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled shouldBe false
      }

      "isEnabledInternal should return false if it has no tags and included tags are set" {
         val yourtag = NamedTag("yourtag")

         val ext = TagExtension { TagExpression(setOf(yourtag), emptySet()) }

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ext)
         }

         val mytag = NamedTag("mytag")
         val test = TestCase(
            name = TestNameBuilder.builder("foo").build(),
            descriptor = IsEnabledTest::class.toDescriptor().append("foo"),
            spec = this@IsEnabledTest,
            parent = null,
            test = {},
            config = TestConfig(tags = setOf(mytag)),
            type = TestType.Test,
         )

         test.isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled shouldBe false
      }

      "isEnabledInternal should return false if it has no tags and a tag expression with include is set" {
         val ext = TagExtension { TagExpression("yourtag") }

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ext)
         }

         val mytag = NamedTag("mytag")
         val test = TestCase(
            name = TestNameBuilder.builder("foo").build(),
            descriptor = IsEnabledTest::class.toDescriptor().append("foo"),
            spec = this@IsEnabledTest,
            parent = null,
            test = {},
            config = TestConfig(tags = setOf(mytag)),
            type = TestType.Test,
         )
         test.isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled shouldBe false
      }

      "isEnabledInternal should return false if the test name begins with a !" {
         val test = TestCase(
            name = TestNameBuilder.builder("!foo").build(),
            descriptor = IsEnabledTest::class.toDescriptor().append("!foo"),
            spec = this@IsEnabledTest,
            parent = null,
            test = {},
            config = null,
            type = TestType.Test,
         )
         test.isEnabledInternal(ProjectConfigResolver(), TestConfigResolver()).isEnabled shouldBe false
      }

      "isEnabledInternal should return false if the test is not focused and the spec contains OTHER focused tests" {
         val test = TestCase(
            name = TestNameBuilder.builder("foo").build(),
            descriptor = IsEnabledWithFocusTest::class.toDescriptor().append("foo"),
            spec = IsEnabledWithFocusTest(),
            parent = null,
            test = {},
            config = null,
            type = TestType.Test,
         )
         test.isEnabledInternal(ProjectConfigResolver(), TestConfigResolver()).isEnabled shouldBe false
      }

      "isEnabledInternal should return true if the test is focused and top level" {
         val test = TestCase(
            name = TestNameBuilder.builder("f:foo").build(),
            descriptor = IsEnabledWithFocusTest::class.toDescriptor().append("f:foo"),
            spec = IsEnabledWithFocusTest(),
            parent = null,
            test = {},
            config = null,
            type = TestType.Test,
         )
         test.isEnabledInternal(ProjectConfigResolver(), TestConfigResolver()).isEnabled shouldBe true
      }

      "isEnabledInternal should return true if not top level even if spec has top level focused tests" {
         val test = TestCase(
            name = TestNameBuilder.builder("f:my test").build(),
            descriptor = IsEnabledWithFocusTest::class.toDescriptor().append("f:my test").append("foo"),
            spec = IsEnabledWithFocusTest(),
            parent = null,
            test = {},
            config = null,
            type = TestType.Test,
         )
         test.isEnabledInternal(ProjectConfigResolver(), TestConfigResolver()).isEnabled shouldBe true
      }

      "isEnabledInternal should return false if a test filter excludes the test" {

         val filter = object : DescriptorFilter {
            override fun filter(descriptor: Descriptor): DescriptorFilterResult {
               return if (descriptor.id.value == "f") DescriptorFilterResult.Include else DescriptorFilterResult.Exclude(null)
            }
         }

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(filter)
         }

         TestCase(
            name = TestNameBuilder.builder("f").build(),
            descriptor = SomeTestClass::class.toDescriptor().append("f"),
            spec = SomeTestClass(),
            parent = null,
            test = {},
            config = null,
            type = TestType.Test,
         ).isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled shouldBe true

         TestCase(
            name = TestNameBuilder.builder("g").build(),
            descriptor = SomeTestClass::class.toDescriptor().append("g"),
            spec = SomeTestClass(),
            parent = null,
            test = {},
            config = null,
            type = TestType.Test,
         ).isEnabledInternal(ProjectConfigResolver(c), TestConfigResolver(c)).isEnabled shouldBe false
      }

      "isEnabled should use extensions when registered" {

         val ext = object : EnabledExtension {
            override suspend fun isEnabled(descriptor: Descriptor) =
               if (descriptor.id.value.contains("activateme"))
                  Enabled.enabled
               else
                  Enabled.disabled("descriptor name does not contain activateme")
         }

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ext)
         }

         // this should be disabled because the extension says it is, even though it's normally enabled
         TestCase(
            name = TestNameBuilder.builder("enabled").build(),
            descriptor = SomeTestClass::class.toDescriptor().append("enabled"),
            spec = SomeTestClass(),
            parent = null,
            test = {},
            config = null,
            type = TestType.Test,
         ).isEnabled(ProjectConfigResolver(c), SpecConfigResolver(c), TestConfigResolver(c)).isEnabled shouldBe false

         TestCase(
            name = TestNameBuilder.builder("activateme").build(),
            descriptor = SomeTestClass::class.toDescriptor().append("activateme"),
            spec = SomeTestClass(),
            parent = null,
            test = {},
            config = null,
            type = TestType.Test,
         ).isEnabled(ProjectConfigResolver(c), SpecConfigResolver(c), TestConfigResolver(c)).isEnabled shouldBe true
      }
   }
}

class SomeTestClass : FunSpec({
   test("f") {}
   test("g") {}
})


class IsEnabledWithFocusTest : FunSpec({
   test("f: focused") {}
   test("not focused") {}
})
