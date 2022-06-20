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
import io.kotest.plugin.intellij.KotestConfiguration
import java.awt.BorderLayout

class KotestSettingsEditor(runConfiguration: KotestConfiguration) :
   JavaSettingsEditorBase<KotestConfiguration>(runConfiguration) {

   override fun createClasspathCombo(): SettingsEditorFragment<KotestConfiguration, ModuleClasspathCombo> {
      val item = ModuleClasspathCombo.Item(ExecutionBundle.message("application.configuration.include.provided.scope"))
      return CommonJavaFragments.moduleClasspath(
         item,
         { false },
         { _, _ -> false },
      )
   }

   override fun customizeFragments(
      fragments: MutableList<SettingsEditorFragment<KotestConfiguration, *>>,
      classpathCombo: ModuleClasspathCombo,
      commonParameterFragments: CommonParameterFragments<KotestConfiguration>
   ) {

      fragments.add(commonParameterFragments.programArguments())

      SpecClassBrowser<EditorTextField>(
         project,
         ConfigurationModuleSelector(project, classpathCombo)
      ).apply {
         setField(specClassTextField)
      }

      PackageChooserActionListener<EditorTextField>(project).apply { setField(packageNameTextField) }

      fragments.addAll(0, listOf(testPathFragment, specClassFragment, packageNameFragment))

      val jreSelector = DefaultJreSelector.fromModuleDependencies(classpathCombo, false)
      val jrePath = CommonJavaFragments.createJrePath<KotestConfiguration>(jreSelector)
      fragments.add(jrePath)
      fragments.add(createShortenClasspath(classpathCombo, jrePath, false))
   }

   private val specClassTextField = EditorTextFieldWithBrowseButton(project, true)
   private val specClassField = LabeledComponent.create(
      /* component = */ specClassTextField,
      /* text = */ KotestBundle.getMessage("spec.class.label"),
      /* labelConstraint = */ BorderLayout.WEST
   )

   private val specClassFragment =
      SettingsEditorFragment<KotestConfiguration, LabeledComponent<EditorTextFieldWithBrowseButton>>(
         /* id = */ "specClass",
         /* name = */ KotestBundle.getMessage("spec.class.name"),
         /* group = */ "Kotest",
         /* component = */ specClassField,
         /* reset = */ { config, field -> field.component.text = config.getSpecName() ?: "" },
         /* apply = */ { configuration, field -> configuration.setSpecName(field.component.text) },
         /* initialSelection = */ { true }
      )

   private val testPathField = LabeledComponent.create(
      TextFieldWithHistory(),
      KotestBundle.getMessage("test.path.label"),
      BorderLayout.WEST
   )

   private val testPathFragment =
      SettingsEditorFragment<KotestConfiguration, LabeledComponent<TextFieldWithHistory>>(
         "testPath",
         KotestBundle.getMessage("test.path.name"),
         "Kotest",
         testPathField,
         { config, field -> field.component.text = config.getTestPath() },
         { configuration, field -> configuration.setTestPath(field.component.text) },
         { true }
      )

   private val packageNameTextField = EditorTextFieldWithBrowseButton(project, false)
   private val packageNameField = LabeledComponent.create(
      packageNameTextField,
      KotestBundle.getMessage("package.label"),
      BorderLayout.WEST
   )

   private val packageNameFragment =
      SettingsEditorFragment<KotestConfiguration, LabeledComponent<EditorTextFieldWithBrowseButton>>(
         "kotestPackageName",
         KotestBundle.getMessage("package.name"),
         "Kotest",
         packageNameField,
         { configuration, field -> field.component.text = configuration.getPackageName() ?: "" },
         { configuration, field -> configuration.setPackageName(field.component.text) },
         { true }
      )
}
