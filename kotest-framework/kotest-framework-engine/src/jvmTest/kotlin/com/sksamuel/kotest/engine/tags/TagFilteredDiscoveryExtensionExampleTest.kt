package com.sksamuel.kotest.engine.tags

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.core.NamedTag
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.annotation.Tags
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.tags.TagExpression
import io.kotest.matchers.booleans.shouldBeFalse

@EnabledIf(LinuxOnlyGithubCondition::class)
class TagFilteredDiscoveryExtensionExampleTest : StringSpec() {
   companion object {
      val ext = TagExtension { TagExpression(emptySet(), setOf(NamedTag("SpecExcluded"))) }
   }

   init {
      "Spec marked with excluded tag should not be run" {
         val collector = CollectingTestEngineListener()
         val c = object : AbstractProjectConfig() {
            override val extensions: List<Extension> = listOf(ext)
         }
         TestEngineLauncher().withListener(collector)
            .withProjectConfig(c)
            .withClasses(ShouldBeExcluded::class)
            .launch()

         collector.errors.shouldBeFalse()
      }
   }
}


@Tags("SpecExcluded")
private class ShouldBeExcluded : StringSpec({
   "Spec marked with a excluded tag" {
     AssertionErrorBuilder.fail("Should never execute (excluded by spec tag)")
   }
})
