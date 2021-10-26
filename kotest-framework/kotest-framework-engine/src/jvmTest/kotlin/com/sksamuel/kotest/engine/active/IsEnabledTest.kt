//package com.sksamuel.kotest.engine.active
//
//import io.kotest.common.ExperimentalKotest
//import io.kotest.core.NamedTag
//import io.kotest.core.Tags
//import io.kotest.core.config.Configuration
//import io.kotest.core.descriptors.Descriptor
//import io.kotest.core.descriptors.append
//import io.kotest.core.descriptors.toDescriptor
//import io.kotest.core.extensions.EnabledExtension
//import io.kotest.core.extensions.TagExtension
//import io.kotest.core.filter.TestFilter
//import io.kotest.core.filter.TestFilterResult
//import io.kotest.core.filter.toTestFilterResult
//import io.kotest.core.names.TestName
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.core.spec.style.StringSpec
//import io.kotest.core.test.Enabled
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.config.TestCaseConfig
//import io.kotest.engine.test.status.isEnabled
//import io.kotest.engine.test.status.isEnabledInternal
//import io.kotest.matchers.shouldBe
//
//@ExperimentalKotest
//class IsEnabledTest : StringSpec() {
//
//   init {
//
//      "isActiveInternal should return false if the test is disabled in config" {
//         val config = TestCaseConfig(enabled = false)
//         val test =
//            TestCase.test(
//               IsEnabledTest::class.toDescriptor().append("foo"),
//               TestName("foo"),
//               this@IsEnabledTest,
//               parent = null
//            ) {}
//               .copy(config = config)
//         test.isEnabledInternal(Configuration()).isEnabled shouldBe false
//      }
//
//      "isActiveInternal should return false if the test is disabled using the isEnabledFn" {
//         val config = TestCaseConfig(enabledIf = { false })
//         val test =
//            TestCase.test(
//               IsEnabledTest::class.toDescriptor().append("foo"),
//               TestName("foo"),
//               this@IsEnabledTest,
//               parent = null
//            ) {}
//               .copy(config = config)
//         test.isEnabledInternal(Configuration()).isEnabled shouldBe false
//      }
//
//      "isEnabledInternal should return true if the test is disabled using the isEnabledFn" {
//         val config = TestCaseConfig(enabledIf = { true })
//         val test =
//            TestCase.test(
//               IsEnabledTest::class.toDescriptor().append("foo"),
//               TestName("foo"),
//               this@IsEnabledTest,
//               parent = null
//            ) {}
//               .copy(config = config)
//         test.isEnabledInternal(Configuration()).isEnabled shouldBe true
//      }
//
//      "isEnabledInternal should return false if it has an excluded tag" {
//
//         val mytag = NamedTag("mytag")
//
//         val ext = object : TagExtension {
//            override fun tags(): Tags =
//               Tags(emptySet(), setOf(mytag))
//         }
//
//         val c = Configuration()
//         c.registry().add(ext)
//
//         val config = TestCaseConfig(tags = setOf(mytag))
//         val test =
//            TestCase.test(
//               IsEnabledTest::class.toDescriptor().append("foo"),
//               TestName("foo"),
//               this@IsEnabledTest,
//               parent = null
//            ) {}.copy(config = config)
//
//         test.isEnabledInternal(c).isEnabled shouldBe false
//      }
//
//      "isEnabledInternal should return false if it is excluded by a tag expression" {
//
//         val mytag = NamedTag("mytag")
//
//         val ext = object : TagExtension {
//            override fun tags(): Tags = Tags("!mytag")
//         }
//
//         val c = Configuration()
//         c.registry().add(ext)
//
//         val config = TestCaseConfig(tags = setOf(mytag))
//         val test =
//            TestCase.test(
//               IsEnabledTest::class.toDescriptor().append("foo"),
//               TestName("foo"),
//               this@IsEnabledTest,
//               parent = null
//            ) {}.copy(config = config)
//
//         test.isEnabledInternal(c).isEnabled shouldBe false
//      }
//
//      "isEnabledInternal should return false if it has no tags and included tags are set" {
//
//         val yourtag = NamedTag("yourtag")
//
//         val ext = object : TagExtension {
//            override fun tags(): Tags = Tags(setOf(yourtag), emptySet())
//         }
//
//         val c = Configuration()
//         c.registry().add(ext)
//
//         val mytag = NamedTag("mytag")
//         val config = TestCaseConfig(tags = setOf(mytag))
//         val test = TestCase.test(
//            IsEnabledTest::class.toDescriptor().append("foo"),
//            TestName("foo"),
//            this@IsEnabledTest,
//            parent = null
//         ) {}
//            .copy(config = config)
//         test.isEnabledInternal(c).isEnabled shouldBe false
//      }
//
//      "isEnabledInternal should return false if it has no tags and a tag expression with include is set" {
//
//         val ext = object : TagExtension {
//            override fun tags(): Tags = Tags("yourtag")
//         }
//
//         val c = Configuration()
//         c.registry().add(ext)
//
//         val mytag = NamedTag("mytag")
//         val config = TestCaseConfig(tags = setOf(mytag))
//         val test = TestCase.test(
//            IsEnabledTest::class.toDescriptor().append("foo"),
//            TestName("foo"),
//            this@IsEnabledTest,
//            parent = null
//         ) {}
//            .copy(config = config)
//         test.isEnabledInternal(c).isEnabled shouldBe false
//      }
//
//      "isEnabledInternal should return false if the test name begins with a !" {
//         val test = TestCase.test(
//            IsEnabledTest::class.toDescriptor().append("!my test"),
//            TestName("!my test"),
//            this@IsEnabledTest,
//            parent = null
//         ) {}
//         test.isEnabledInternal(Configuration()).isEnabled shouldBe false
//      }
//
//      "isEnabledInternal should return false if the test is not focused and the spec contains OTHER focused tests" {
//         val test = TestCase.test(
//            IsEnabledWithFocusTest::class.toDescriptor().append("my test"),
//            TestName("my test"),
//            IsEnabledWithFocusTest(),
//            parent = null
//         ) {}
//         test.isEnabledInternal(Configuration()).isEnabled shouldBe false
//      }
//
//      "isEnabledInternal should return true if the test is focused and top level" {
//         val test = TestCase.test(
//            IsEnabledWithFocusTest::class.toDescriptor().append("f:my test"),
//            TestName("f:my test"),
//            IsEnabledWithFocusTest(),
//            parent = null
//         ) {}
//         test.isEnabledInternal(Configuration()).isEnabled shouldBe true
//      }
//
//      "isEnabledInternal should return true if not top level even if spec has top level focused tests" {
//         val test =
//            TestCase.test(
//               IsEnabledWithFocusTest::class.toDescriptor().append("f:my test").append("foo"),
//               TestName("f:my test"),
//               IsEnabledWithFocusTest(),
//               parent = null
//            ) {}
//         test.isEnabledInternal(Configuration()).isEnabled shouldBe true
//      }
//
//      "isEnabledInternal should return false if a test filter excludes the test" {
//
//         val filter = object : TestFilter {
//            override fun filter(descriptor: Descriptor): TestFilterResult {
//               return (descriptor.id.value == "f").toTestFilterResult(null)
//            }
//         }
//
//         val c = Configuration()
//         c.registry().add(filter)
//
//         TestCase.test(
//            SomeTestClass::class.toDescriptor().append("f"),
//            TestName("f"),
//            SomeTestClass(),
//            parent = null
//         ) {}.isEnabledInternal(c).isEnabled shouldBe true
//
//         TestCase.test(
//            SomeTestClass::class.toDescriptor().append("g"),
//            TestName("g"),
//            SomeTestClass(),
//            parent = null
//         ) {}.isEnabledInternal(c).isEnabled shouldBe false
//      }
//
//      "isEnabled should use extensions when registered" {
//
//         val ext = object : EnabledExtension {
//            override suspend fun isEnabled(descriptor: Descriptor) =
//               if (descriptor.id.value.contains("activateme"))
//                  Enabled.enabled
//               else
//                  Enabled.disabled("descriptor name does not contain activateme")
//         }
//
//         val c = Configuration()
//         c.registry().add(ext)
//
//         // this should be disabled because the extension says it is, even though it's normally enabled
//         TestCase.test(
//            SomeTestClass::class.toDescriptor().append("enabled"),
//            TestName("enabled"),
//            SomeTestClass(),
//            parent = null
//         ) {}.isEnabled(c).isEnabled shouldBe false
//      }
//   }
//}
//
//class SomeTestClass : FunSpec({
//   test("f") {}
//   test("g") {}
//})
//
//
//class IsEnabledWithFocusTest : FunSpec({
//   test("f: focused") {}
//   test("not focused") {}
//})
