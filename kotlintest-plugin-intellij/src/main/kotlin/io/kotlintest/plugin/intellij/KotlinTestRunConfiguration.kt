package io.kotlintest.plugin.intellij

import com.intellij.execution.CommonJavaRunConfigurationParameters
import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.JavaRunConfigurationModule
import com.intellij.execution.configurations.ModuleBasedConfiguration
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor
import java.util.*

class KotlinTestRunConfiguration(name: String, module: JavaRunConfigurationModule, factory: ConfigurationFactory) :
    ModuleBasedConfiguration<JavaRunConfigurationModule, RunConfigurationOptions>(name, module, factory),
    CommonJavaRunConfigurationParameters {

  private var alternativeJrePath: String? = ""
  private var alternativeJrePathEnabled = false
  private var envs = mutableMapOf<String, String>()
  private var passParentEnvs: Boolean = false
  private var programParameters: String? = null
  private var vmParameters: String? = ""
  private var workingDirectory: String? = null
  private var testName: String? = null
  private var specName: String? = null

  override fun suggestedName(): String? {
    val fqn = specName
    return if (fqn == null) testName else {
      val simpleName = fqn.split('.').last()
      "$simpleName: $testName"
    }
  }

  fun getTestName(): String? = testName
  fun getSpecName(): String? = specName

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
  override fun getValidModules(): MutableCollection<Module> {
    return Arrays.asList(*ModuleManager.getInstance(project).modules)
  }

  override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? =
      KotlinTestCommandLineState(environment, this)

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

  fun setTestName(testName: String) {
    this.testName = testName
  }

  fun setSpecName(specName: String) {
    this.specName = specName
  }

  override fun setPassParentEnvs(passParentEnvs: Boolean) {
    this.passParentEnvs = passParentEnvs
  }

  override fun setProgramParameters(programParameteres: String?) {
    this.programParameters = programParameteres
  }
}