package io.kotlintest.intellij

import com.intellij.execution.CommonJavaRunConfigurationParameters
import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.application.BaseJavaApplicationCommandLineState
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.configurations.JavaRunConfigurationModule
import com.intellij.execution.configurations.ModuleBasedConfiguration
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor

class KotlinTestRunConfiguration(name: String,
                                 module: JavaRunConfigurationModule,
                                 factory: ConfigurationFactory) : ModuleBasedConfiguration<JavaRunConfigurationModule>(name, module, factory), CommonJavaRunConfigurationParameters {

  var specFQN: String? = null

  private var alternativeJrePathEnabled = false
  private var alternativeJrePath: String? = ""
  private var envs = mutableMapOf<String, String>()
  private var passParentEnvs: Boolean = false
  private var programParameters: String? = null
  private var vmParameters: String? = ""
  private var workingDirectory: String? = null

  override fun setAlternativeJrePath(path: String?) {
    this.alternativeJrePath = path
  }

  override fun isAlternativeJrePathEnabled(): Boolean = alternativeJrePathEnabled
  override fun isPassParentEnvs(): Boolean = passParentEnvs
  override fun getAlternativeJrePath(): String? = alternativeJrePath
  override fun getEnvs(): MutableMap<String, String> = envs
  override fun getProgramParameters(): String? = programParameters
  override fun getPackage(): String? = null
  override fun getRunClass(): String? = null
  override fun getVMParameters() = vmParameters
  override fun getWorkingDirectory(): String? = null

  override fun setProgramParameters(value: String?) {
    this.programParameters = value
  }

  override fun setVMParameters(value: String?) {
    this.vmParameters = value
  }

  override fun setAlternativeJrePathEnabled(enabled: Boolean) {
    alternativeJrePathEnabled = enabled
  }

  override fun setWorkingDirectory(value: String?) {
    this.workingDirectory = value
  }

  override fun setEnvs(envs: MutableMap<String, String>) {
    this.envs = envs
  }

  override fun setPassParentEnvs(passParentEnvs: Boolean) {
    this.passParentEnvs = passParentEnvs
  }

  override fun getValidModules(): Collection<Module> = ModuleManager.getInstance(project).modules.toList()

  override fun suggestedName(): String? {
    val fq = specFQN
    return if (fq == null) "Run test"
    else "Run $fq"
  }

  override fun checkConfiguration() {
  }

  override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = TODO()

  override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? =
      object : BaseJavaApplicationCommandLineState<KotlinTestRunConfiguration>(environment, this) {

        override fun createJavaParameters(): JavaParameters {
          return JavaParameters()
        }

        fun createConsole(executor: Executor, processHandler: ProcessHandler): ConsoleView {
          val consoleProperties = SMTRunnerConsoleProperties(this@KotlinTestRunConfiguration, "KotlinTest", executor)
          return SMTestRunnerConnectionUtil.createAndAttachConsole(
              "KotlinTest",
              processHandler,
              consoleProperties
          )
        }

        override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
          val processHandler = startProcess()
          val console = createConsole(executor, processHandler)
          return DefaultExecutionResult(console, processHandler, *createActions(console, processHandler, executor))
        }
      }
}