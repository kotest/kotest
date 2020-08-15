package io.kotest.plugin.pitest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.DiscoveryExtension
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

class ProjectConfig : AbstractProjectConfig() {
   override fun extensions(): List<Extension> = listOf(object : DiscoveryExtension {
      override fun afterScan(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
         return if (classes.size == 1) classes else classes.filterNot { it.qualifiedName!!.contains("Specs") }
      }
   })
}
