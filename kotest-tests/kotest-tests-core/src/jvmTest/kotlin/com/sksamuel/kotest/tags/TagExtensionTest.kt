package com.sksamuel.kotest.tags

import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.config.Project
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.extensions.TagExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import kotlin.reflect.KClass

class TagExtensionTest : StringSpec() {

   object TagA : Tag()
   object TagB : Tag()

   private val ext = object : TagExtension {
      override fun tags(): Tags =
         Tags(setOf(TagA), setOf(TagB))
   }


   override fun beforeSpec(spec: Spec) {
      Project.registerExtension(ext)
   }

   override fun afterSpec(spec: Spec) {
      Project.deregisterExtension(ext)
   }

   init {

      listener(object : TestListener {
         override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
            results.map { it.key.displayName to it.value.status }.toMap() shouldBe mapOf(
               "should be tagged with tagA and therefore included" to TestStatus.Success,
               "should be untagged and therefore excluded" to TestStatus.Ignored,
               "should be tagged with tagB and therefore excluded" to TestStatus.Ignored
            )
         }
      })

      "should be tagged with tagA and therefore included".config(tags = setOf(TagA)) { }

      "should be untagged and therefore excluded" { }

      "should be tagged with tagB and therefore excluded".config(tags = setOf(TagB)) { }
   }
}
