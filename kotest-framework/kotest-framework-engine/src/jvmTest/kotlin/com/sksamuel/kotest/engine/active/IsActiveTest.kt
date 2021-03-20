package com.sksamuel.kotest.engine.active

import io.kotest.core.NamedTag
import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.core.extensions.IsActiveExtension
import io.kotest.core.extensions.TagExtension
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.filter.toTestFilterResult
import io.kotest.core.internal.isActive
import io.kotest.core.internal.isActiveInternal
import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.Description
import io.kotest.core.test.IsActive
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.matchers.shouldBe

@Isolate
class IsActiveTest : StringSpec() {

   init {

      "isActiveInternal should return false if the test is disabled in config" {
         val config = TestCaseConfig(enabled = false)
         val test =
            TestCase.test(IsActiveTest::class.toDescription().appendTest("foo"), this@IsActiveTest, parent = null) {}
               .copy(config = config)
         test.isActiveInternal().active shouldBe false
      }

      "isActiveInternal should return false if the test is disabled using the isEnabledFn" {
         val config = TestCaseConfig(enabledIf = { false })
         val test =
            TestCase.test(IsActiveTest::class.toDescription().appendTest("foo"), this@IsActiveTest, parent = null) {}
               .copy(config = config)
         test.isActiveInternal().active shouldBe false
      }

      "isActiveInternal should return true if the test is disabled using the isEnabledFn" {
         val config = TestCaseConfig(enabledIf = { true })
         val test =
            TestCase.test(IsActiveTest::class.toDescription().appendTest("foo"), this@IsActiveTest, parent = null) {}
               .copy(config = config)
         test.isActiveInternal().active shouldBe true
      }

      "isActiveInternal should return false if it has an excluded tag" {

         val mytag = NamedTag("mytag")

         val ext = object : TagExtension {
            override fun tags(): Tags =
               Tags(emptySet(), setOf(mytag))
         }

         configuration.registerExtension(ext)

         val config = TestCaseConfig(tags = setOf(mytag))
         val test =
            TestCase.test(IsActiveTest::class.toDescription().appendTest("foo"), this@IsActiveTest, parent = null) {}
               .copy(config = config)
         test.isActiveInternal().active shouldBe false

         configuration.deregisterExtension(ext)
      }

      "isActiveInternal should return false if it is excluded by a tag expression" {

         val mytag = NamedTag("mytag")

         val ext = object : TagExtension {
            override fun tags(): Tags = Tags("!mytag")
         }

         configuration.registerExtension(ext)

         val config = TestCaseConfig(tags = setOf(mytag))
         val test =
            TestCase.test(IsActiveTest::class.toDescription().appendTest("foo"), this@IsActiveTest, parent = null) {}
               .copy(config = config)
         test.isActiveInternal().active shouldBe false

         configuration.deregisterExtension(ext)
      }

      "isActiveInternal should return false if it has no tags and included tags are set" {

         val yourtag = NamedTag("yourtag")

         val ext = object : TagExtension {
            override fun tags(): Tags = Tags(setOf(yourtag), emptySet())
         }

         configuration.registerExtension(ext)

         val mytag = NamedTag("mytag")
         val config = TestCaseConfig(tags = setOf(mytag))
         val test = TestCase.test(
            IsActiveTest::class.toDescription().appendTest("foo"),
            this@IsActiveTest,
            parent = null
         ) {}
            .copy(config = config)
         test.isActiveInternal().active shouldBe false

         configuration.deregisterExtension(ext)
      }

      "isActiveInternal should return false if it has no tags and a tag expression with include is set" {

         val ext = object : TagExtension {
            override fun tags(): Tags = Tags("yourtag")
         }

         configuration.registerExtension(ext)

         val mytag = NamedTag("mytag")
         val config = TestCaseConfig(tags = setOf(mytag))
         val test = TestCase.test(
            IsActiveTest::class.toDescription().appendTest("foo"),
            this@IsActiveTest,
            parent = null
         ) {}
            .copy(config = config)
         test.isActiveInternal().active shouldBe false

         configuration.deregisterExtension(ext)
      }

      "isActiveInternal should return false if the test name begins with a !" {
         val test = TestCase.test(
            IsActiveTest::class.toDescription().appendTest("!my test"),
            this@IsActiveTest,
            parent = null
         ) {}
         test.isActiveInternal().active shouldBe false
      }

      "isActiveInternal should return false if the test is not focused and the spec contains OTHER focused tests" {
         val test = TestCase.test(
            IsActiveWithFocusTest::class.toDescription().appendTest("my test"),
            IsActiveWithFocusTest(),
            parent = null
         ) {}
         test.isActiveInternal().active shouldBe false
      }

      "isActiveInternal should return true if the test is focused and top level" {
         val test = TestCase.test(
            IsActiveWithFocusTest::class.toDescription().appendTest("f:my test"),
            IsActiveWithFocusTest(),
            parent = null
         ) {}
         test.isActiveInternal().active shouldBe true
      }

      "isActiveInternal should return true if not top level even if spec has top level focused tests" {
         val test =
            TestCase.test(
               IsActiveWithFocusTest::class.toDescription().appendTest("f:my test").appendTest("foo"),
               IsActiveWithFocusTest(),
               parent = null
            ) {}
         test.isActiveInternal().active shouldBe true
      }

      "isActiveInternal should return false if a test filter excludes the test" {
         val filter = object : TestFilter {
            override fun filter(description: Description): TestFilterResult {
               return (description.displayName() == "f").toTestFilterResult()
            }
         }
         configuration.registerFilter(filter)

         TestCase.test(
            SomeTestClass::class.toDescription().appendTest("f"),
            SomeTestClass(),
            parent = null
         ) {}.isActiveInternal().active shouldBe true

         TestCase.test(
            SomeTestClass::class.toDescription().appendTest("g"),
            SomeTestClass(),
            parent = null
         ) {}.isActiveInternal().active shouldBe false

         configuration.deregisterFilter(filter)
      }

      "isActive should use extensions when registered" {

         val ext = object : IsActiveExtension {
            override suspend fun isActive(descriptor: Descriptor) =
               if (descriptor.name.value.contains("activateme"))
                  IsActive.active
               else
                  IsActive.inactive("descriptor name does not contain activateme")
         }

         configuration.registerExtension(ext)

         // this should be inactive because the extension says it is, even though it's normally active
         TestCase.test(
            SomeTestClass::class.toDescription().appendTest("active"),
            SomeTestClass(),
            parent = null
         ) {}.isActive().active shouldBe false

//         // this should be active because the extension says it is, even though it's disabled by a bang
//         TestCase.test(
//            SomeTestClass::class.toDescription().appendTest("!activateme"),
//            SomeTestClass()
//         ) {}.isActive() shouldBe true

         configuration.deregisterExtension(ext)
      }
   }
}

class SomeTestClass : FunSpec({
   test("f") {}
   test("g") {}
})


class IsActiveWithFocusTest : FunSpec({
   test("f: focused") {}
   test("not focused") {}
})
