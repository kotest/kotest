package io.kotlintest.plugin.intellij

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.JavaRunConfigurationModule
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader

object Icons {
  val KotlinTest16 = IconLoader.getIcon("/icon16.png")
  val KotlinTest32 = IconLoader.getIcon("/icon32.png")
}

class KotlinTestConfigurationType : ConfigurationTypeBase(
    "io.kotlintest.jvm",
    "KotlinTest",
    "Run tests with KotlinTest",
    Icons.KotlinTest16) {

  init {
    addFactory(KotlinTestConfigurationFactory(this))
  }

  private class KotlinTestConfigurationFactory(configurationType: ConfigurationType) : ConfigurationFactory(configurationType) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
      return KotlinTestRunConfiguration("KotlinTest", JavaRunConfigurationModule(project, true), this)
    }
  }
}