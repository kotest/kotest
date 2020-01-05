package io.kotest.provided

import io.kotest.core.AbstractProjectConfig
import io.kotest.core.specs.SpecContainer
import io.kotest.extensions.DiscoveryExtension
import io.kotest.extensions.ProjectLevelExtension

class ProjectConfig : AbstractProjectConfig() {
   override fun extensions(): List<ProjectLevelExtension> =
      listOf(object : DiscoveryExtension {
         override fun afterScan(classes: List<SpecContainer>): List<SpecContainer> {
            return if (classes.size == 1) classes else classes.filterNot { it.name.value.contains("TestCase") }
         }
      })
}
