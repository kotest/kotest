package io.kotest.plugin.intellij

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

class KotestConfigurationFactory(configurationType: ConfigurationType) : ConfigurationFactory(configurationType) {

   override fun createTemplateConfiguration(project: Project): RunConfiguration {
      return KotestRunConfiguration(Constants().FrameworkName, this, project)
   }

   override fun getId(): String = Constants().FrameworkName
}
