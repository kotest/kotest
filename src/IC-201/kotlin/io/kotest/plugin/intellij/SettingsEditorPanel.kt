package io.kotest.plugin.intellij

import com.intellij.application.options.ModuleDescriptionsComboBox
import com.intellij.execution.ui.CommonJavaParametersPanel
import com.intellij.execution.ui.ConfigurationModuleSelector
import com.intellij.execution.ui.DefaultJreSelector
import com.intellij.execution.ui.JrePathEditor
import com.intellij.execution.ui.ShortenCommandLineModeCombo
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.ui.TextFieldWithHistory
import javax.swing.JPanel

class SettingsEditorPanel(project: Project) : SettingsEditor<KotestConfiguration>() {

   private lateinit var panel: JPanel

   private lateinit var commonJavaParameters: CommonJavaParametersPanel

   private lateinit var jrePathEditor: JrePathEditor

   private lateinit var testPath: LabeledComponent<TextFieldWithHistory>

   private lateinit var specName: LabeledComponent<TextFieldWithHistory>

   private lateinit var packageName: LabeledComponent<TextFieldWithHistory>

   private lateinit var module: LabeledComponent<ModuleDescriptionsComboBox>

   private lateinit var myShortenClasspathModeCombo: LabeledComponent<ShortenCommandLineModeCombo>

   private var moduleSelector: ConfigurationModuleSelector

   init {
      moduleSelector = ConfigurationModuleSelector(project, module.component)
      jrePathEditor.setDefaultJreSelector(DefaultJreSelector.fromModuleDependencies(module.component, false))
      commonJavaParameters.setModuleContext(moduleSelector.module)
      commonJavaParameters.setHasModuleMacro()
      module.component.addActionListener {
         commonJavaParameters.setModuleContext(moduleSelector.module)
      }

      val shortenCombo = object : ShortenCommandLineModeCombo(project, jrePathEditor, module.component) {}
      myShortenClasspathModeCombo.component = shortenCombo
   }

   override fun resetEditorFrom(configuration: KotestConfiguration) {
      module.component.selectedModule = configuration.configurationModule.module
      moduleSelector.reset(configuration)
      commonJavaParameters.reset(configuration)
      testPath.component.text = configuration.getTestPath()
      specName.component.text = configuration.getSpecName()
      packageName.component.text = configuration.getPackageName()
      jrePathEditor.setPathOrName(configuration.alternativeJrePath, configuration.isAlternativeJrePathEnabled)
      myShortenClasspathModeCombo.component.selectedItem = configuration.shortenCommandLine
   }

   override fun applyEditorTo(configuration: KotestConfiguration) {
      configuration.alternativeJrePath = jrePathEditor.jrePathOrName
      configuration.isAlternativeJrePathEnabled = jrePathEditor.isAlternativeJreSelected
      configuration.setModule(module.component.selectedModule)
      moduleSelector.applyTo(configuration)
      commonJavaParameters.applyTo(configuration)
      configuration.setTestPath(testPath.component.text)
      configuration.setSpecName(specName.component.text)
      configuration.setPackageName(packageName.component.text)
      configuration.shortenCommandLine = myShortenClasspathModeCombo.component.selectedItem
   }

   override fun createEditor() = panel

   private fun createUIComponents() {
      testPath = LabeledComponent.create(
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
      testPath.component.isEditable = true
      specName.component.isEditable = true
      packageName.component.isEditable = true
      myShortenClasspathModeCombo = LabeledComponent()
   }
}
