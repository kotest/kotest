package io.kotest.plugin.intellij.ui

import com.intellij.execution.ExecutionBundle
import com.intellij.execution.application.JavaSettingsEditorBase
import com.intellij.execution.ui.CommonJavaFragments
import com.intellij.execution.ui.CommonParameterFragments
import com.intellij.execution.ui.ConfigurationModuleSelector
import com.intellij.execution.ui.DefaultJreSelector
import com.intellij.execution.ui.ModuleClasspathCombo
import com.intellij.execution.ui.SettingsEditorFragment
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.ui.EditorTextField
import com.intellij.ui.EditorTextFieldWithBrowseButton
import com.intellij.ui.TextFieldWithHistory
import io.kotest.plugin.intellij.run.idea.KotestRunConfiguration
import java.awt.BorderLayout
import java.util.function.BiConsumer

class KotestSettingsEditor(runConfiguration: KotestRunConfiguration) :
   JavaSettingsEditorBase<KotestRunConfiguration>(runConfiguration) {

   override fun customizeFragments(
      fragments: MutableList<SettingsEditorFragment<KotestRunConfiguration, *>>,
      moduleClasspath: SettingsEditorFragment<KotestRunConfiguration, ModuleClasspathCombo>,
      unused: CommonParameterFragments<KotestRunConfiguration>?,
   ) {
      SpecClassBrowser<EditorTextField>(
         project,
         ConfigurationModuleSelector(project, moduleClasspath.component())
      ).apply {
         setField(specClassTextField)
      }

      PackageChooserActionListener<EditorTextField>(project).apply { setField(packageNameTextField) }

      fragments.addAll(0, listOf(testPathFragment, specClassFragment, packageNameFragment))

      if (!project.isDefault) {
         val fragment = SettingsEditorFragment.createTag(
            "test.use.module.path",
            ExecutionBundle.message("do.not.use.module.path.tag"),
            "Java",
            { !it.isUseModulePath },
            BiConsumer<KotestRunConfiguration, Boolean> { config, value -> config.isUseModulePath = !value }
         )
         fragments.add(fragment)
      }

      fragments.add(moduleClasspath)

      val jreSelector = DefaultJreSelector.fromModuleDependencies(moduleClasspath.component(), false)
      val jrePath = CommonJavaFragments.createJrePath<KotestRunConfiguration>(jreSelector)
      fragments.add(createShortenClasspath(moduleClasspath.component(), jrePath, false))
      fragments.add(jrePath)
   }

   private val specClassTextField = EditorTextFieldWithBrowseButton(project, true)
   private val specClassField = LabeledComponent.create(
      specClassTextField,
      KotestBundle().getMessage("spec.class.label"),
      BorderLayout.WEST
   )

   private val specClassFragment =
      SettingsEditorFragment<KotestRunConfiguration, LabeledComponent<EditorTextFieldWithBrowseButton>>(
         "specClass",
         KotestBundle().getMessage("spec.class.name"),
         "Kotest",
         specClassField,
         { config, field -> field.component.text = config.getSpecName() ?: "" },
         { configuration, field -> configuration.setSpecName(field.component.text) },
         { true }
      )

   private val testPathField = LabeledComponent.create(
      TextFieldWithHistory(),
      KotestBundle().getMessage("test.path.label"),
      BorderLayout.WEST
   )

   private val testPathFragment =
      SettingsEditorFragment<KotestRunConfiguration, LabeledComponent<TextFieldWithHistory>>(
         "testPath",
         KotestBundle().getMessage("test.path.name"),
         "Kotest",
         testPathField,
         { config, field -> field.component.text = config.getTestPath() },
         { configuration, field -> configuration.setTestPath(field.component.text) },
         { true }
      )

   private val packageNameTextField = EditorTextFieldWithBrowseButton(project, false)
   private val packageNameField = LabeledComponent.create(
      packageNameTextField,
      KotestBundle().getMessage("package.label"),
      BorderLayout.WEST
   )

   private val packageNameFragment =
      SettingsEditorFragment<KotestRunConfiguration, LabeledComponent<EditorTextFieldWithBrowseButton>>(
         "kotestPackageName",
         KotestBundle().getMessage("package.name"),
         "Kotest",
         packageNameField,
         { configuration, field -> field.component.text = configuration.getPackageName() ?: "" },
         { configuration, field -> configuration.setPackageName(field.component.text) },
         { true }
      )
}
