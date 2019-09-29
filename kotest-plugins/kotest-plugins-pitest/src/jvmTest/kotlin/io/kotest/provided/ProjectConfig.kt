package io.kotest.provided

import io.kotest.AbstractProjectConfig
import io.kotest.Spec
import io.kotest.extensions.DiscoveryExtension
import io.kotest.extensions.ProjectLevelExtension
import kotlin.reflect.KClass

class ProjectConfig : AbstractProjectConfig() {
  override fun extensions(): List<ProjectLevelExtension> =
      listOf(object : DiscoveryExtension {
        override fun afterScan(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
          return if (classes.size == 1) classes else classes.filterNot { it.qualifiedName!!.contains("Specs") }
        }
      })
}