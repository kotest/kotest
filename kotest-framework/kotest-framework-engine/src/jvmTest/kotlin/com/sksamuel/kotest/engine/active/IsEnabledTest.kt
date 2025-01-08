package com.sksamuel.kotest.engine.active

import io.kotest.common.ExperimentalKotest
import io.kotest.core.NamedTag
import io.kotest.engine.tags.TagExpression
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.append
import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.extensions.TagExtension
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.filter.toTestFilterResult
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.test.config.ResolvedTestConfig
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.status.isEnabled
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.matchers.shouldBe

@ExperimentalKotest
class IsEnabledTest : StringSpec() {

   init {

      "isEnabledInternal should return false if the test is disabled in config" {
         val test = TestCase(
            name =  TestNameBuilder.builder("foo").build(),
            descriptor = IsEnabledTest::class.toDescriptor().append("foo"),
            spec = this@IsEnabledTest,
            parent = null,
            test = {},
            config = ResolvedTestConfig.default.copy(enabled = { Enabled.disabled }),
            type = TestType.Test,
         )
         test.isEnabledInternal(ProjectConfiguration()).isEnabled shouldBe false
      }

      "isEnabledInternal should return false if it has an excluded tag" {

         val mytag = NamedTag("mytag")

         val ext = object : TagExtension {
            override fun tags(): TagExpression =
               TagExpression(emptySet(), setOf(mytag))
         }

         val c = ProjectConfiguration()
         c.registry.add(ext)

         val test = TestCase(
            name =  TestNameBuilder.builder("foo").build(),
            descriptor = IsEnabledTest::class.toDescriptor().append("foo"),
            spec = this@IsEnabledTest,
            parent = null,
            test = {},
            config = ResolvedTestConfig.default.copy(tags = setOf(mytag)),
            type = TestType.Test,
         )

         test.isEnabledInternal(c).isEnabled shouldBe false
      }

      "isEnabledInternal should return false if it is excluded by a tag expression" {
         val mytag = NamedTag("mytag")

         val ext = object : TagExtension {
            override fun tags(): TagExpression = TagExpression("!mytag")
         }

         val c = ProjectConfiguration()
         c.registry.add(ext)

         val test = TestCase(
            name =  TestNameBuilder.builder("foo").build(),
            descriptor = IsEnabledTest::class.toDescriptor().append("foo"),
            spec = this@IsEnabledTest,
            parent = null,
            test = {},
            config = ResolvedTestConfig.default.copy(tags = setOf(mytag)),
            type = TestType.Test,
         )

         test.isEnabledInternal(c).isEnabled shouldBe false
      }

      "isEnabledInternal should return false if it has no tags and included tags are set" {
         val yourtag = NamedTag("yourtag")

         val ext = object : TagExtension {
            override fun tags(): TagExpression = TagExpression(setOf(yourtag), emptySet())
         }

         val c = ProjectConfiguration()
         c.registry.add(ext)

         val mytag = NamedTag("mytag")
         val test = TestCase(
            name =  TestNameBuilder.builder("foo").build(),
            descriptor = IsEnabledTest::class.toDescriptor().append("foo"),
            spec = this@IsEnabledTest,
            parent = null,
            test = {},
            config = ResolvedTestConfig.default.copy(tags = setOf(mytag)),
            type = TestType.Test,
         )

         test.isEnabledInternal(c).isEnabled shouldBe false
      }

      "isEnabledInternal should return false if it has no tags and a tag expression with include is set" {
         val ext = object : TagExtension {
            override fun tags(): TagExpression = TagExpression("yourtag")
         }

         val c = ProjectConfiguration()
         c.registry.add(ext)

         val mytag = NamedTag("mytag")
         val test = TestCase(
            name =  TestNameBuilder.builder("foo").build(),
            descriptor = IsEnabledTest::class.toDescriptor().append("foo"),
            spec = this@IsEnabledTest,
            parent = null,
            test = {},
            config = ResolvedTestConfig.default.copy(tags = setOf(mytag)),
            type = TestType.Test,
         )
         test.isEnabledInternal(c).isEnabled shouldBe false
      }

      "isEnabledInternal should return false if the test name begins with a !" {
         val test = TestCase(
            name = TestNameBuilder.builder("!foo").build(),
            descriptor = IsEnabledTest::class.toDescriptor().append("!foo"),
            spec = this@IsEnabledTest,
            parent = null,
            test = {},
            config = ResolvedTestConfig.default,
            type = TestType.Test,
         )
         test.isEnabledInternal(ProjectConfiguration()).isEnabled shouldBe false
      }

      "isEnabledInternal should return false if the test is not focused and the spec contains OTHER focused tests" {
         val test = TestCase(
            name =  TestNameBuilder.builder("foo").build(),
            descriptor = IsEnabledWithFocusTest::class.toDescriptor().append("foo"),
            spec = IsEnabledWithFocusTest(),
            parent = null,
            test = {},
            config = ResolvedTestConfig.default,
            type = TestType.Test,
         )
         test.isEnabledInternal(ProjectConfiguration()).isEnabled shouldBe false
      }

      "isEnabledInternal should return true if the test is focused and top level" {
         val test = TestCase(
            name = TestNameBuilder.builder("f:foo").build(),
            descriptor = IsEnabledWithFocusTest::class.toDescriptor().append("f:foo"),
            spec = IsEnabledWithFocusTest(),
            parent = null,
            test = {},
            config = ResolvedTestConfig.default,
            type = TestType.Test,
         )
         test.isEnabledInternal(ProjectConfiguration()).isEnabled shouldBe true
      }

      "isEnabledInternal should return true if not top level even if spec has top level focused tests" {
         val test = TestCase(
            name = TestNameBuilder.builder("f:my test").build(),
            descriptor = IsEnabledWithFocusTest::class.toDescriptor().append("f:my test").append("foo"),
            spec = IsEnabledWithFocusTest(),
            parent = null,
            test = {},
            config = ResolvedTestConfig.default,
            type = TestType.Test,
         )
         test.isEnabledInternal(ProjectConfiguration()).isEnabled shouldBe true
      }

      "isEnabledInternal should return false if a test filter excludes the test" {

         val filter = object : TestFilter {
            override fun filter(descriptor: Descriptor): TestFilterResult {
               return (descriptor.id.value == "f").toTestFilterResult(null)
            }
         }

         val c = ProjectConfiguration()
         c.registry.add(filter)

         TestCase(
            name = TestNameBuilder.builder("f").build(),
            descriptor = SomeTestClass::class.toDescriptor().append("f"),
            spec = SomeTestClass(),
            parent = null,
            test = {},
            config = ResolvedTestConfig.default,
            type = TestType.Test,
         ).isEnabledInternal(c).isEnabled shouldBe true

         TestCase(
            name = TestNameBuilder.builder("g").build(),
            descriptor = SomeTestClass::class.toDescriptor().append("g"),
            spec = SomeTestClass(),
            parent = null,
            test = {},
            config = ResolvedTestConfig.default,
            type = TestType.Test,
         ).isEnabledInternal(c).isEnabled shouldBe false
      }

      "isEnabled should use extensions when registered" {

         val ext = object : EnabledExtension {
            override suspend fun isEnabled(descriptor: Descriptor) =
               if (descriptor.id.value.contains("activateme"))
                  Enabled.enabled
               else
                  Enabled.disabled("descriptor name does not contain activateme")
         }

         val c = ProjectConfiguration()
         c.registry.add(ext)

         // this should be disabled because the extension says it is, even though it's normally enabled
         TestCase(
            name = TestNameBuilder.builder("enabled").build(),
            descriptor = SomeTestClass::class.toDescriptor().append("enabled"),
            spec = SomeTestClass(),
            parent = null,
            test = {},
            config = ResolvedTestConfig.default,
            type = TestType.Test,
         ).isEnabled(c).isEnabled shouldBe false

         TestCase(
            name = TestNameBuilder.builder("activateme").build(),
            descriptor = SomeTestClass::class.toDescriptor().append("activateme"),
            spec = SomeTestClass(),
            parent = null,
            test = {},
            config = ResolvedTestConfig.default,
            type = TestType.Test,
         ).isEnabled(c).isEnabled shouldBe true
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
