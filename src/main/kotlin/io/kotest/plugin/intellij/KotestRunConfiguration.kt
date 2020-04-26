package io.kotest.plugin.intellij

import com.intellij.execution.CommonJavaRunConfigurationParameters
import com.intellij.execution.Executor
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ModuleBasedConfiguration
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationModule
import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.sm.runner.SMRunnerConsolePropertiesProvider
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizerUtil
import io.kotest.plugin.intellij.notifications.DependencyChecker
import io.kotest.plugin.intellij.psi.buildSuggestedName
import org.jdom.Element
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotestRunConfiguration(name: String, configurationFactory: ConfigurationFactory, project: Project) :
   ModuleBasedConfiguration<RunConfigurationModule, RunConfigurationOptions>(
      name,
      RunConfigurationModule(project),
      configurationFactory
   ), CommonJavaRunConfigurationParameters, SMRunnerConsolePropertiesProvider {

   private var alternativeJrePath: String? = ""
   private var alternativeJrePathEnabled = false
   private var envs = mutableMapOf<String, String>()
   private var passParentEnvs: Boolean = true
   private var programParameters: String? = null
   private var vmParameters: String? = ""
   private var workingDirectory: String? = null
   private var testName: String? = null
   private var specName: String? = null
   private var packageName: String? = null

   override fun suggestedName(): String? = buildSuggestedName(specName, testName, packageName)

   fun getTestName(): String? = testName
   fun getSpecName(): String? = specName
   fun getPackageName(): String? = packageName

   override fun isPassParentEnvs(): Boolean = passParentEnvs
   override fun isAlternativeJrePathEnabled() = alternativeJrePathEnabled
   override fun getEnvs(): MutableMap<String, String> = envs
   override fun getPackage(): String? = null
   override fun getRunClass(): String? = null
   override fun getVMParameters(): String? = vmParameters
   override fun getAlternativeJrePath() = alternativeJrePath
   override fun getProgramParameters(): String? = programParameters
   override fun getWorkingDirectory(): String? = workingDirectory

   override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
      SettingsEditorPanel(project)

   // let all modules be valid
   override fun getValidModules(): MutableCollection<Module> =
      ModuleManager.getInstance(project).modules.toMutableList()

   override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
      return when {
         configurationModule.module == null -> null
         !DependencyChecker.checkMissingDependencies(configurationModule.module!!) -> null
         else -> KotestCommandLineState(environment, this)
      }
   }

   override fun setAlternativeJrePath(path: String?) {
      alternativeJrePath = path
   }

   override fun setVMParameters(value: String?) {
      vmParameters = value
   }

   override fun setAlternativeJrePathEnabled(enabled: Boolean) {
      alternativeJrePathEnabled = enabled
   }

   override fun setWorkingDirectory(workingDirectory: String?) {
      this.workingDirectory = workingDirectory
   }

   override fun setEnvs(envs: MutableMap<String, String>) {
      this.envs = envs
   }

   fun setTestName(testName: String?) {
      this.testName = testName
   }

   fun setSpecName(specName: String?) {
      this.specName = specName
   }

   fun setSpec(spec: KtClassOrObject?) {
      this.specName = spec?.fqName?.asString()
   }

   fun setPackageName(packageName: String?) {
      this.packageName = packageName
   }

   override fun setPassParentEnvs(passParentEnvs: Boolean) {
      this.passParentEnvs = passParentEnvs
   }

   override fun setProgramParameters(programParameteres: String?) {
      this.programParameters = programParameteres
   }

   override fun getActionName(): String? = when {
      packageName?.isNotBlank() ?: false -> "All tests in '$packageName'"
      testName?.isNotBlank() ?: false -> testName
      specName?.isNotBlank() ?: false -> specName
      else -> super.getActionName()
   }

   override fun writeExternal(element: Element) {
      super.writeExternal(element)
      JDOMExternalizerUtil.writeField(element, PassParentEnvsField, passParentEnvs.toString())
      JDOMExternalizerUtil.writeField(element, WorkingDirField, workingDirectory)
      JDOMExternalizerUtil.writeField(element, ProgramParamsField, programParameters)
      JDOMExternalizerUtil.writeField(element, SpecNameField, specName)
      JDOMExternalizerUtil.writeField(element, TestNameField, testName)
      JDOMExternalizerUtil.writeField(element, PackageNameField, packageName)
      EnvironmentVariablesComponent.writeExternal(element, envs)
   }

   override fun readExternal(element: Element) {
      super.readExternal(element)
      passParentEnvs = JDOMExternalizerUtil.readField(element, PassParentEnvsField, "false").toBoolean()
      workingDirectory = JDOMExternalizerUtil.readField(element, WorkingDirField)
      programParameters = JDOMExternalizerUtil.readField(element, ProgramParamsField)
      specName = JDOMExternalizerUtil.readField(element, SpecNameField)
      testName = JDOMExternalizerUtil.readField(element, TestNameField)
      packageName = JDOMExternalizerUtil.readField(element, PackageNameField)
      EnvironmentVariablesComponent.readExternal(element, envs)
   }

   override fun createTestConsoleProperties(executor: Executor): SMTRunnerConsoleProperties {
      return KotestSMTConsoleProperties(this, executor)
   }

   companion object {
      const val PassParentEnvsField = "passParentEnvs"
      const val ProgramParamsField = "programParameters"
      const val WorkingDirField = "workingDirectory"
      const val PackageNameField = "packageName"
      const val TestNameField = "testName"
      const val SpecNameField = "specName"
   }
}
