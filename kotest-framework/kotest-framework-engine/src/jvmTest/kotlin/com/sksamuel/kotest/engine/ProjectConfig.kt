package com.sksamuel.kotest.engine

import com.sksamuel.kotest.engine.tags.TagFilteredDiscoveryExtensionExampleTest
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension

class ProjectConfig : AbstractProjectConfig() {

   override fun extensions(): List<Extension> = listOf(
      TagFilteredDiscoveryExtensionExampleTest.ext,
   )

   override val parallelism: Int = 1
}
