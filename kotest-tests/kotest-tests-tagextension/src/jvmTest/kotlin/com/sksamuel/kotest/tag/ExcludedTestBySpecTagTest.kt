package com.sksamuel.kotest.tag

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.core.Tag
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.tags.TagExpression

object Exclude : Tag()

object ExcludeTagExtension : TagExtension {
   override fun tags(): TagExpression = TagExpression.exclude(Exclude)
}

class ProjectConfig : AbstractProjectConfig() {
   override val extensions = listOf(ExcludeTagExtension)
}

/**
 * Tests that a test inheriting a tag from a spec level tags function, is excluded when the
 * active tags are provided by a tag extension.
 */
class ExcludedTestByInlineTagTest : StringSpec({

   tags(Exclude)

   "should not run" {
      AssertionErrorBuilder.fail("Shouldn't get here")
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
         AssertionErrorBuilder.fail("Shouldn't get here")
      }
   }
}

