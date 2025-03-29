package com.sksamuel.kotest.engine.tags

import io.kotest.core.Tag
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.tags.TagExpression
import io.kotest.extensions.system.OverrideMode
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe

@EnabledIf(NotMacOnGithubCondition::class)
class TagExtensionTest : StringSpec() {

   init {
      "tag extensions should be used when calculating runtime tags" {

         withSystemProperty("kotest.tags", null, mode = OverrideMode.SetOrOverride) {
            val c = object : AbstractProjectConfig() {
               override val extensions = listOf(
                  TagExtension { TagExpression(setOf(TagA), setOf(TagB)) },
                  SpecifiedTagsTagExtension(TagExpression("!SpecExcluded"))
               )
            }

            val collector = CollectingTestEngineListener()
            TestEngineLauncher(collector)
               .withClasses(TestWithTags::class)
               .withProjectConfig(c)
               .launch()

            collector.tests.mapKeys { it.key.name.name }.mapValues { it.value.reasonOrNull } shouldBe
               mapOf(
                  "should be tagged with tagA and therefore included" to null,
                  "should be untagged and therefore excluded" to "Disabled by tags: (TagA) & (!TagB) & !SpecExcluded",
                  "should be tagged with tagB and therefore excluded" to
                     "Disabled by tags: (TagA) & (!TagB) & !SpecExcluded"
               )
         }
      }
   }
}

object TagA : Tag()
object TagB : Tag()

private class TestWithTags : StringSpec() {
   init {
      "should be tagged with tagA and therefore included".config(tags = setOf(TagA)) { }

      "should be untagged and therefore excluded" { }

      "should be tagged with tagB and therefore excluded".config(tags = setOf(TagB)) { }
   }
}
