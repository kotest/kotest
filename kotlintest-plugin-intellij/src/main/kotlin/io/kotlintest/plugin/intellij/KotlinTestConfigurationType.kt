package io.kotlintest.plugin.intellij

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.JavaRunConfigurationModule
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.KotlinIcons

class KotlinTestConfigurationType : ConfigurationTypeBase(
    "io.kotlintest.jvm",
    "KotlinTest",
    "Run tests with KotlinTest",
    KotlinIcons.LAUNCH) {

  init {
    addFactory(KotlinTestConfigurationFactory(this))
  }

  private class KotlinTestConfigurationFactory(configurationType: ConfigurationType) : ConfigurationFactory(configurationType) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
      return KotlinTestRunConfiguration("some name here", JavaRunConfigurationModule(project, true), this)
    }
  }
}