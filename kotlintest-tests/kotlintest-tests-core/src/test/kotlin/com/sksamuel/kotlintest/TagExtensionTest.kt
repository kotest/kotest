package com.sksamuel.kotlintest

import io.kotlintest.Project
import io.kotlintest.Tag
import io.kotlintest.Tags
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.extensions.TagExtension
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TagExtensionTest : StringSpec() {

  object TagA : Tag()
  object TagB : Tag()

  private val ext = object : TagExtension {
    override fun tags(): Tags = Tags(setOf(TagA), setOf(TagB))
  }

  override fun afterTest(testCase: TestCase, result: TestResult) {
    when (testCase.name) {
      "should be tagged with tagA" -> result.status shouldBe TestStatus.Success
      "should be untagged" -> result.status shouldBe TestStatus.Ignored
      "should be tagged with tagB" -> result.status shouldBe TestStatus.Ignored
    }
  }


  init {

    Project.registerExtension(ext)

    "should be tagged with tagA".config(tags = setOf(TagA)) { }

    "should be untagged" { }

    "should be tagged with tagB".config(tags = setOf(TagB)) { }

    Project.deregisterExtension(ext)

  }
}