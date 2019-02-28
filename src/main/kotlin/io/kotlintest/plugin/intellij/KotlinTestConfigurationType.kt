package io.kotlintest.plugin.intellij

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object Icons {
  val KotlinTest16 = IconLoader.getIcon("/icon16.png")
  val KotlinTest32 = IconLoader.getIcon("/icon32.png")
}

class KotlinTestConfigurationType : ConfigurationType, DumbAware {
  private val factory = KotlinTestConfigurationFactory(this)

  override fun getIcon(): Icon = Icons.KotlinTest16

  override fun getConfigurationTypeDescription(): String = "Run tests with KotlinTest"

  override fun getId(): String = "io.kotlintest.jvm"

  override fun getDisplayName(): String = "KotlinTest"

  override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(factory)
}

class KotlinTestConfigurationFactory(configurationType: ConfigurationType) : ConfigurationFactory(configurationType) {
  override fun createTemplateConfiguration(project: Project): RunConfiguration {
    return KotlinTestRunConfiguration("KotlinTest", this, project)
  }
}