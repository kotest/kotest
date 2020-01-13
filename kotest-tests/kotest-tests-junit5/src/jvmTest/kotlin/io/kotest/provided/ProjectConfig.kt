package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.extensions.DiscoveryExtension
import io.kotest.core.extensions.ProjectLevelExtension
import kotlin.reflect.KClass

class ProjectConfig : AbstractProjectConfig() {
  override fun extensions(): List<ProjectLevelExtension> =
      listOf(object : DiscoveryExtension {
        override fun afterScan(classes: List<KClass<out SpecConfiguration>>): List<KClass<out SpecConfiguration>> {
          return if (classes.size == 1) classes else classes.filterNot { it.qualifiedName!!.contains("TestCase") }
        }
      })
}
