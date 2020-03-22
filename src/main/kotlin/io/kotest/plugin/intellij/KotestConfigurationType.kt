package io.kotest.plugin.intellij

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object Icons {
  val Kotest16 = IconLoader.getIcon("/icon16.png")
  val Kotest32 = IconLoader.getIcon("/icon32.png")
}

class KotestConfigurationType : ConfigurationType, DumbAware {

  private val factory = KotestConfigurationFactory(this)

  override fun getIcon(): Icon = Icons.Kotest16

  override fun getConfigurationTypeDescription(): String = "Run tests with Kotest"

  override fun getId(): String = "io.kotest.jvm"

  override fun getDisplayName(): String = "Kotest"

  override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(factory)
}

class KotestConfigurationFactory(configurationType: ConfigurationType) : ConfigurationFactory(configurationType) {
  override fun createTemplateConfiguration(project: Project): RunConfiguration {
    return KotestRunConfiguration("Kotest", this, project)
  }
}