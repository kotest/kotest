package com.sksamuel.kotlintest

import io.kotlintest.Project
import io.kotlintest.Spec
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

  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    results.map { it.key.name to it.value.status }.toMap() shouldBe mapOf(
        "should be tagged with tagA and therefore included" to TestStatus.Success,
        "should be untagged and therefore excluded" to TestStatus.Ignored,
        "should be tagged with tagB and therefore excluded" to TestStatus.Ignored
    )
  }

  override fun beforeSpec(spec: Spec) {
    Project.registerExtension(ext)
  }

  override fun afterSpec(spec: Spec) {
    Project.deregisterExtension(ext)
  }

  init {
    "should be tagged with tagA and therefore included".config(tags = setOf(TagA)) { }

    "should be untagged and therefore excluded" { }

    "should be tagged with tagB and therefore excluded".config(tags = setOf(TagB)) { }
  }
}