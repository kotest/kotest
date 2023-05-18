package com.sksamuel.kotest.engine.tags

import io.kotest.assertions.fail
import io.kotest.core.NamedTag
import io.kotest.core.annotation.Tags
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.booleans.shouldBeFalse

class TagFilteredDiscoveryExtensionExampleTest : StringSpec() {
   companion object {
      val ext = object : TagExtension {
         override fun tags(): io.kotest.core.TagExpression =
            io.kotest.core.TagExpression(emptySet(), setOf(NamedTag("SpecExcluded")))
      }
   }

   init {
      "Spec marked with excluded tag should not be run" {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withConfiguration(ProjectConfiguration().apply { registry.add(ext) })
            .withClasses(ShouldBeExcluded::class)
            .launch()

         collector.errors.shouldBeFalse()
      }
   }
}


@Tags("SpecExcluded")
private class ShouldBeExcluded : StringSpec({
   "Spec marked with a excluded tag" {
      fail("Should never execute (excluded by spec tag)")
   }
})
