package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.Spec
import io.kotlintest.extensions.DiscoveryExtension
import io.kotlintest.extensions.ProjectLevelExtension
import kotlin.reflect.KClass

class ProjectConfig : AbstractProjectConfig() {
  override fun extensions(): List<ProjectLevelExtension> =
      listOf(object : DiscoveryExtension {
        override fun afterScan(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
          return if (classes.size == 1) classes else classes.filterNot { it.qualifiedName!!.contains("Specs") }
        }
      })
}