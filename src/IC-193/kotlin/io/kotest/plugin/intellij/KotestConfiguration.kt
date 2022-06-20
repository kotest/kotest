package io.kotest.plugin.intellij

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.project.Project

class KotestConfiguration(name: String, factory: ConfigurationFactory, project: Project) :
   KotestConfigurationBase(name, factory, project) {
   override fun getConfigurationEditor() = SettingsEditorPanel(project)
}
