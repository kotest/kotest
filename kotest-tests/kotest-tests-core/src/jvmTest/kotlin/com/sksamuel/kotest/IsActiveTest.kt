package com.sksamuel.kotest

import io.kotest.core.test.Description
import io.kotest.Project
import io.kotest.core.StringTag
import io.kotest.core.Tags
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.extensions.TagExtension
import io.kotest.core.test.isActive
import io.kotest.core.spec.FunSpec
import io.kotest.core.spec.StringSpec
import io.kotest.shouldBe

class IsActiveTest : StringSpec() {

   init {

      "isActive should return false if the test is disabled in config" {
         val config = TestCaseConfig(enabled = false)
         val test = TestCase.test(Description.spec("foo"), this@IsActiveTest) {}.copy(config = config)
         test.isActive() shouldBe false
      }

      "isActive should return false if it has an excluded tag" {

         val mytag = StringTag("mytag")

         val ext = object : TagExtension {
            override fun tags(): Tags =
               Tags(emptySet(), setOf(mytag))
         }

         Project.registerExtension(ext)

         val config = TestCaseConfig(tags = setOf(mytag))
         val test = TestCase.test(Description.spec("foo"), this@IsActiveTest) {}.copy(config = config)
         test.isActive() shouldBe false

         Project.deregisterExtension(ext)
      }

      "isActive should return false if it has no tags and included tags are set" {

         val yourtag = StringTag("yourtag")

         val ext = object : TagExtension {
            override fun tags(): Tags =
               Tags(setOf(yourtag), emptySet())
         }

         Project.registerExtension(ext)

         val mytag = StringTag("mytag")
         val config = TestCaseConfig(tags = setOf(mytag))
         val test = TestCase.test(Description.spec("foo"), this@IsActiveTest) {}.copy(config = config)
         test.isActive() shouldBe false

         Project.deregisterExtension(ext)
      }

      "isActive should return false if the test name begins with a !" {
         val test = TestCase.test(Description.spec("spec").append("!my test"), this@IsActiveTest) {}
         test.isActive() shouldBe false
      }

      "isActive should return false if the test is not focused and the spec contains OTHER focused tests" {
         val test = TestCase.test(Description.spec("spec").append("my test"), IsActiveWithFocusTest()) {}
         test.isActive() shouldBe false
      }

      "isActive should return true if the test is focused" {
         val test = TestCase.test(Description.spec("spec").append("f:my test"), IsActiveWithFocusTest()) {}
         test.isActive() shouldBe true
      }
   }
}

class IsActiveWithFocusTest : FunSpec({
   test("f: focused") {}
   test("not focused") {}
})
