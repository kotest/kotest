package com.sksamuel.kotlintest.tests

import io.kotlintest.Description
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.Tag
import io.kotlintest.Tags
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.extensions.TagExtension
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TagExtensionTest : StringSpec() {

  var addTags = false

  val ext = object : TagExtension {
    override fun tags(): Tags = if (addTags) Tags(setOf(TagA), setOf(TagB)) else Tags.Empty
  }

  override fun beforeSpec(description: Description, spec: Spec) {
    addTags = true
  }

  override fun afterSpec(description: Description, spec: Spec) {
    addTags = false
  }

  override fun afterTest(description: Description, result: TestResult) {
    when (description.name) {
      "should be tagged with tagA" -> result.status shouldBe TestStatus.Success
      "should be untagged" -> result.status shouldBe TestStatus.Ignored
      "should be tagged with tagB" -> result.status shouldBe TestStatus.Ignored
      else -> {
      }
    }
    if (description.name == "test TagExtension")
      addTags = false
  }

  object TagA : Tag()
  object TagB : Tag()

  init {

    Project.registerExtension(ext)

    "should be tagged with tagA".config(tags = setOf(TagA)) { }

    "should be untagged" { }

    "should be tagged with tagB".config(tags = setOf(TagB)) { }
  }
}