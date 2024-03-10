package com.sksamuel.kotest.tag

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.Tag
import io.kotest.core.TagExpression
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec

object Exclude : Tag()

object ExcludeTagExtension : TagExtension {
   override fun tags(): TagExpression = TagExpression.exclude(Exclude)
}

class ProjectConfig : AbstractProjectConfig() {
   override fun extensions() = listOf(ExcludeTagExtension)
}

/**
 * Tests that a test inheriting a tag from a spec level tags function, is excluded when the
 * active tags are provided by a tag extension.
 */
class ExcludedTestByInlineTagTest : StringSpec({

   tags(Exclude)

   "should not run" {
      fail("Shouldn't get here")
   }
})

/**
 * Tests that a test inheriting a tag from a spec level tags function, is excluded when the
 * active tags are provided by a tag extension.
 */
class ExcludedTestByOverrideTagTest : StringSpec() {

   override fun tags(): Set<Tag> {
      return setOf(Exclude)
   }

   init {
      "should not run" {
         fail("Shouldn't get here")
      }
   }
}

class TagLifecycleTest : FunSpec() {
   init {
      test("tags added from tests should not be allowed as the spec is already past resolution at that point") {
         shouldThrowAny {
            tags(Exclude)
         }
      }
   }
}
