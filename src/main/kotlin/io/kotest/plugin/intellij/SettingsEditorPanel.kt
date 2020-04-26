package io.kotest.plugin.intellij

import com.intellij.application.options.ModulesComboBox
import com.intellij.execution.ui.CommonJavaParametersPanel
import com.intellij.execution.ui.ConfigurationModuleSelector
import com.intellij.execution.ui.DefaultJreSelector
import com.intellij.execution.ui.JrePathEditor
import com.intellij.openapi.module.Module
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.ui.TextFieldWithHistory
import javax.swing.JPanel

class SettingsEditorPanel(project: Project) : SettingsEditor<KotestRunConfiguration>() {

   private lateinit var panel: JPanel

   private lateinit var mainPanel: JPanel

   private lateinit var commonJavaParameters: CommonJavaParametersPanel

   private lateinit var module: LabeledComponent<ModulesComboBox>

   private lateinit var jrePathEditor: JrePathEditor

   private lateinit var testName: LabeledComponent<TextFieldWithHistory>

   private lateinit var specName: LabeledComponent<TextFieldWithHistory>

   private lateinit var packageName: LabeledComponent<TextFieldWithHistory>

   private var moduleSelector: ConfigurationModuleSelector

   private var selectedModule: Module?
      get() {
         return module.component.selectedModule
      }
      set(value) {
         module.component.selectedModule = value
      }

   init {
      module.component.fillModules(project)
      moduleSelector = ConfigurationModuleSelector(project, module.component)
      jrePathEditor.setDefaultJreSelector(DefaultJreSelector.fromModuleDependencies(module.component, false))
      commonJavaParameters.setModuleContext(selectedModule)
      commonJavaParameters.setHasModuleMacro()
      module.component.addActionListener {
         commonJavaParameters.setModuleContext(selectedModule)
      }
   }

   override fun resetEditorFrom(configuration: KotestRunConfiguration) {
      selectedModule = configuration.configurationModule.module
      moduleSelector.reset(configuration)
      commonJavaParameters.reset(configuration)
      testName.component.text = configuration.getTestName()
      specName.component.text = configuration.getSpecName()
      packageName.component.text = configuration.getPackageName()
   }

   override fun applyEditorTo(configuration: KotestRunConfiguration) {
      configuration.setModule(selectedModule)
      moduleSelector.applyTo(configuration)
      commonJavaParameters.applyTo(configuration)
      configuration.setTestName(testName.component.text)
      configuration.setSpecName(specName.component.text)
      configuration.setPackageName(packageName.component.text)
   }

   override fun createEditor() = panel

   private fun createUIComponents() {
      testName = LabeledComponent.create(
         TextFieldWithHistory(),
         "Scope", "West"
      )
      specName = LabeledComponent.create(
         TextFieldWithHistory(),
         "Scope", "West"
      )
      packageName = LabeledComponent.create(
         TextFieldWithHistory(),
         "Scope", "West"
      )
      testName.component.isEditable = true
      specName.component.isEditable = true
      packageName.component.isEditable = true
   }
}
