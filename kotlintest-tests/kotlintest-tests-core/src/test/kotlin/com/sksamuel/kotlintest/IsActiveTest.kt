package com.sksamuel.kotlintest

import io.kotlintest.Description
import io.kotlintest.Project
import io.kotlintest.StringTag
import io.kotlintest.Tags
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestType
import io.kotlintest.extensions.TagExtension
import io.kotlintest.internal.isActive
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class IsActiveTest : StringSpec() {

  init {

    "isActive should return false if the test is disabled in config" {
      val config = TestCaseConfig(enabled = false)
      val test = TestCase(Description.root("foo"), this@IsActiveTest, {}, 1, TestType.Test, config)
      isActive(test) shouldBe false
    }

    "isActive should return false if it has an excluded tag" {

      val mytag = StringTag("mytag")

      val ext = object : TagExtension {
        override fun tags(): Tags = Tags(emptySet(), setOf(mytag))
      }

      Project.registerExtension(ext)

      val config = TestCaseConfig(tags = setOf(mytag))
      val test = TestCase(Description.root("foo"), this@IsActiveTest, {}, 1, TestType.Test, config)
      isActive(test) shouldBe false

      Project.deregisterExtension(ext)
    }

    "isActive should return false if it has no tags and included tags are set" {

      val yourtag = StringTag("yourtag")

      val ext = object : TagExtension {
        override fun tags(): Tags = Tags(setOf(yourtag), emptySet())
      }

      Project.registerExtension(ext)

      val mytag = StringTag("mytag")
      val config = TestCaseConfig(tags = setOf(mytag))
      val test = TestCase(Description.root("foo"), this@IsActiveTest, {}, 1, TestType.Test, config)
      isActive(test) shouldBe false

      Project.deregisterExtension(ext)
    }

    "isActive should return false if the test name begins with a !" {
      val config = TestCaseConfig()
      val test = TestCase(Description.root("!my test"), this@IsActiveTest, {}, 1, TestType.Test, config)
      isActive(test) shouldBe false
    }

  }
}