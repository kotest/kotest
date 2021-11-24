package com.sksamuel.kotest.engine.tags

import io.kotest.assertions.fail
import io.kotest.core.NamedTag
import io.kotest.core.annotation.Tags
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.style.StringSpec

@Tags("SpecExcluded")
class TagFilteredDiscoveryExtensionExampleTest : StringSpec() {

   companion object {
      val ext = object : TagExtension {
         override fun tags(): io.kotest.core.TagExpression =
            io.kotest.core.TagExpression(emptySet(), setOf(NamedTag("SpecExcluded")))
      }
   }

   init {

      "Spec marked with a excluded tag" {
         fail("Should never execute (excluded by spec tag)")
      }
   }
}
