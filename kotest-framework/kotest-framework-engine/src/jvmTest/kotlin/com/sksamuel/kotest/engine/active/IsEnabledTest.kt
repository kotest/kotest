package com.sksamuel.kotest.engine.active

import io.kotest.common.ExperimentalKotest
import io.kotest.core.NamedTag
import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.extensions.TagExtension
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.filter.toTestFilterResult
import io.kotest.engine.test.status.isEnabled
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.Description
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.matchers.shouldBe

@ExperimentalKotest
@Isolate
class IsEnabledTest : StringSpec() {

   init {

      "isActiveInternal should return false if the test is disabled in config" {
         val config = TestCaseConfig(enabled = false)
         val test =
            TestCase.test(IsEnabledTest::class.toDescription().appendTest("foo"), this@IsEnabledTest, parent = null) {}
               .copy(config = config)
         test.isEnabledInternal().isEnabled shouldBe false
      }

      "isActiveInternal should return false if the test is disabled using the isEnabledFn" {
         val config = TestCaseConfig(enabledIf = { false })
         val test =
            TestCase.test(IsEnabledTest::class.toDescription().appendTest("foo"), this@IsEnabledTest, parent = null) {}
               .copy(config = config)
         test.isEnabledInternal().isEnabled shouldBe false
      }

      "isEnabledInternal should return true if the test is disabled using the isEnabledFn" {
         val config = TestCaseConfig(enabledIf = { true })
         val test =
            TestCase.test(IsEnabledTest::class.toDescription().appendTest("foo"), this@IsEnabledTest, parent = null) {}
               .copy(config = config)
         test.isEnabledInternal().isEnabled shouldBe true
      }

      "isEnabledInternal should return false if it has an excluded tag" {

         val mytag = NamedTag("mytag")

         val ext = object : TagExtension {
            override fun tags(): Tags =
               Tags(emptySet(), setOf(mytag))
         }

         configuration.registerExtension(ext)

         val config = TestCaseConfig(tags = setOf(mytag))
         val test =
            TestCase.test(IsEnabledTest::class.toDescription().appendTest("foo"), this@IsEnabledTest, parent = null) {}
               .copy(config = config)
         test.isEnabledInternal().isEnabled shouldBe false

         configuration.deregisterExtension(ext)
      }

      "isEnabledInternal should return false if it is excluded by a tag expression" {

         val mytag = NamedTag("mytag")

         val ext = object : TagExtension {
            override fun tags(): Tags = Tags("!mytag")
         }

         configuration.registerExtension(ext)

         val config = TestCaseConfig(tags = setOf(mytag))
         val test =
            TestCase.test(IsEnabledTest::class.toDescription().appendTest("foo"), this@IsEnabledTest, parent = null) {}
               .copy(config = config)
         test.isEnabledInternal().isEnabled shouldBe false

         configuration.deregisterExtension(ext)
      }

      "isEnabledInternal should return false if it has no tags and included tags are set" {

         val yourtag = NamedTag("yourtag")

         val ext = object : TagExtension {
            override fun tags(): Tags = Tags(setOf(yourtag), emptySet())
         }

         configuration.registerExtension(ext)

         val mytag = NamedTag("mytag")
         val config = TestCaseConfig(tags = setOf(mytag))
         val test = TestCase.test(
            IsEnabledTest::class.toDescription().appendTest("foo"),
            this@IsEnabledTest,
            parent = null
         ) {}
            .copy(config = config)
         test.isEnabledInternal().isEnabled shouldBe false

         configuration.deregisterExtension(ext)
      }

      "isEnabledInternal should return false if it has no tags and a tag expression with include is set" {

         val ext = object : TagExtension {
            override fun tags(): Tags = Tags("yourtag")
         }

         configuration.registerExtension(ext)

         val mytag = NamedTag("mytag")
         val config = TestCaseConfig(tags = setOf(mytag))
         val test = TestCase.test(
            IsEnabledTest::class.toDescription().appendTest("foo"),
            this@IsEnabledTest,
            parent = null
         ) {}
            .copy(config = config)
         test.isEnabledInternal().isEnabled shouldBe false

         configuration.deregisterExtension(ext)
      }

      "isEnabledInternal should return false if the test name begins with a !" {
         val test = TestCase.test(
            IsEnabledTest::class.toDescription().appendTest("!my test"),
            this@IsEnabledTest,
            parent = null
         ) {}
         test.isEnabledInternal().isEnabled shouldBe false
      }

      "isEnabledInternal should return false if the test is not focused and the spec contains OTHER focused tests" {
         val test = TestCase.test(
            IsEnabledWithFocusTest::class.toDescription().appendTest("my test"),
            IsEnabledWithFocusTest(),
            parent = null
         ) {}
         test.isEnabledInternal().isEnabled shouldBe false
      }

      "isEnabledInternal should return true if the test is focused and top level" {
         val test = TestCase.test(
            IsEnabledWithFocusTest::class.toDescription().appendTest("f:my test"),
            IsEnabledWithFocusTest(),
            parent = null
         ) {}
         test.isEnabledInternal().isEnabled shouldBe true
      }

      "isEnabledInternal should return true if not top level even if spec has top level focused tests" {
         val test =
            TestCase.test(
               IsEnabledWithFocusTest::class.toDescription().appendTest("f:my test").appendTest("foo"),
               IsEnabledWithFocusTest(),
               parent = null
            ) {}
         test.isEnabledInternal().isEnabled shouldBe true
      }

      "isEnabledInternal should return false if a test filter excludes the test" {
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
         ) {}.isEnabledInternal().isEnabled shouldBe true

         TestCase.test(
            SomeTestClass::class.toDescription().appendTest("g"),
            SomeTestClass(),
            parent = null
         ) {}.isEnabledInternal().isEnabled shouldBe false

         configuration.deregisterFilter(filter)
      }

      "isEnabled should use extensions when registered" {

         val ext = object : EnabledExtension {
            override suspend fun isEnabled(descriptor: Descriptor) =
               if (descriptor.name.value.contains("activateme"))
                  Enabled.enabled
               else
                  Enabled.disabled("descriptor name does not contain activateme")
         }

         configuration.registerExtension(ext)

         // this should be disabled because the extension says it is, even though it's normally enabled
         TestCase.test(
            SomeTestClass::class.toDescription().appendTest("enabled"),
            SomeTestClass(),
            parent = null
         ) {}.isEnabled().isEnabled shouldBe false

//         // this should be isEnabled because the extension says it is, even though it's disabled by a bang
//         TestCase.test(
//            SomeTestClass::class.toDescription().appendTest("!activateme"),
//            SomeTestClass()
//         ) {}.isEnabled() shouldBe true

         configuration.deregisterExtension(ext)
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
