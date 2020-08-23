package com.sksamuel.kotest.engine.active

import io.kotest.core.NamedTag
import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.extensions.TagExtension
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.filter.toTestFilterResult
import io.kotest.core.internal.isActive
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.Description
import io.kotest.matchers.shouldBe

class IsActiveTest : StringSpec() {

   init {

      "isActive should return false if the test is disabled in config" {
         val config = TestCaseConfig(enabled = false)
         val test = TestCase.test(IsActiveTest::class.toDescription().appendTest("foo"), this@IsActiveTest) {}
            .copy(config = config)
         test.isActive() shouldBe false
      }

      "isActive should return false if the test is disabled using the isEnabledFn" {
         val config = TestCaseConfig(enabledIf = { false })
         val test = TestCase.test(IsActiveTest::class.toDescription().appendTest("foo"), this@IsActiveTest) {}
            .copy(config = config)
         test.isActive() shouldBe false
      }

      "isActive should return true if the test is disabled using the isEnabledFn" {
         val config = TestCaseConfig(enabledIf = { true })
         val test = TestCase.test(IsActiveTest::class.toDescription().appendTest("foo"), this@IsActiveTest) {}
            .copy(config = config)
         test.isActive() shouldBe true
      }

      "isActive should return false if it has an excluded tag" {

         val mytag = NamedTag("mytag")

         val ext = object : TagExtension {
            override fun tags(): Tags =
               Tags(emptySet(), setOf(mytag))
         }

         configuration.registerExtension(ext)

         val config = TestCaseConfig(tags = setOf(mytag))
         val test = TestCase.test(IsActiveTest::class.toDescription().appendTest("foo"), this@IsActiveTest) {}
            .copy(config = config)
         test.isActive() shouldBe false

         configuration.deregisterExtension(ext)
      }

      "isActive should return false if it is excluded by a tag expression" {

         val mytag = NamedTag("mytag")

         val ext = object : TagExtension {
            override fun tags(): Tags = Tags("!mytag")
         }

         configuration.registerExtension(ext)

         val config = TestCaseConfig(tags = setOf(mytag))
         val test = TestCase.test(IsActiveTest::class.toDescription().appendTest("foo"), this@IsActiveTest) {}
            .copy(config = config)
         test.isActive() shouldBe false

         configuration.deregisterExtension(ext)
      }

      "isActive should return false if it has no tags and included tags are set" {

         val yourtag = NamedTag("yourtag")

         val ext = object : TagExtension {
            override fun tags(): Tags = Tags(setOf(yourtag), emptySet())
         }

         configuration.registerExtension(ext)

         val mytag = NamedTag("mytag")
         val config = TestCaseConfig(tags = setOf(mytag))
         val test = TestCase.test(IsActiveTest::class.toDescription().appendTest("foo"), this@IsActiveTest) {}
            .copy(config = config)
         test.isActive() shouldBe false

         configuration.deregisterExtension(ext)
      }

      "isActive should return false if it has no tags and a tag expression with include is set" {

         val ext = object : TagExtension {
            override fun tags(): Tags = Tags("yourtag")
         }

         configuration.registerExtension(ext)

         val mytag = NamedTag("mytag")
         val config = TestCaseConfig(tags = setOf(mytag))
         val test = TestCase.test(IsActiveTest::class.toDescription().appendTest("foo"), this@IsActiveTest) {}
            .copy(config = config)
         test.isActive() shouldBe false

         configuration.deregisterExtension(ext)
      }

      "isActive should return false if the test name begins with a !" {
         val test = TestCase.test(
            IsActiveTest::class.toDescription().appendTest("!my test"),
            this@IsActiveTest
         ) {}
         test.isActive() shouldBe false
      }

      "isActive should return false if the test is not focused and the spec contains OTHER focused tests" {
         val test = TestCase.test(
            IsActiveWithFocusTest::class.toDescription().appendTest("my test"),
            IsActiveWithFocusTest()
         ) {}
         test.isActive() shouldBe false
      }

      "isActive should return true if the test is focused and top level" {
         val test = TestCase.test(
            IsActiveWithFocusTest::class.toDescription().appendTest("f:my test"),
            IsActiveWithFocusTest()
         ) {}
         test.isActive() shouldBe true
      }

      "isActive should return true if not top level even if spec has top level focused tests" {
         val test =
            TestCase.test(
               IsActiveWithFocusTest::class.toDescription().appendTest("f:my test").appendTest("foo"),
               IsActiveWithFocusTest()
            ) {}
         test.isActive() shouldBe true
      }

      "isActive should return false if a test filter excludes the test" {
         val filter = object : TestFilter {
            override fun filter(description: Description): TestFilterResult {
               return (description.displayName() == "f").toTestFilterResult()
            }
         }
         configuration.registerFilter(filter)

         TestCase.test(
            SomeTestClass::class.toDescription().appendTest("f"),
            SomeTestClass()
         ) {}.isActive() shouldBe true

         TestCase.test(
            SomeTestClass::class.toDescription().appendTest("g"),
            SomeTestClass()
         ) {}.isActive() shouldBe false

         configuration.deregisterFilter(filter)
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
