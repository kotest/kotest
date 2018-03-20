package io.kotlintest.intellij

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.JavaRunConfigurationModule
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

class KotlinTestConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
  override fun createTemplateConfiguration(project: Project): RunConfiguration =
      KotlinTestRunConfiguration("Spec", JavaRunConfigurationModule(project, true), this)
}