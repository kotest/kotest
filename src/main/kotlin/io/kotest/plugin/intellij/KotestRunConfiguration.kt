@file:Suppress("RedundantOverride")

package io.kotest.plugin.intellij

import com.intellij.execution.AlternativeJrePathConverter
import com.intellij.execution.Executor
import com.intellij.execution.JavaTestConfigurationBase
import com.intellij.execution.Location
import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.JavaRunConfigurationModule
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.target.LanguageRuntimeType
import com.intellij.execution.target.TargetEnvironmentAwareRunProfile
import com.intellij.execution.target.TargetEnvironmentConfiguration
import com.intellij.execution.testframework.TestSearchScope
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.refactoring.listeners.RefactoringElementListener
import io.kotest.plugin.intellij.run.KotestRunnableState
import io.kotest.plugin.intellij.run.RunData
import io.kotest.plugin.intellij.run.suggestedName
import io.kotest.plugin.intellij.ui.KotestSettingsEditor
import org.jdom.Element
import org.jetbrains.jps.model.serialization.PathMacroUtil
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotestRunConfiguration(name: String, factory: ConfigurationFactory, project: Project) :
   JavaTestConfigurationBase(name, JavaRunConfigurationModule(project, false), factory),
   TargetEnvironmentAwareRunProfile {

   private var alternativeJrePath: String? = ""
   private var alternativeJrePathEnabled = false
   private var envs = mutableMapOf<String, String>()
   private var passParentEnvs: Boolean = true
   private var programParameters: String? = null
   private var vmParameters: String? = ""
   private var workingDirectory: String? = PathMacroUtil.MODULE_WORKING_DIR
   private var testPath: String? = null
   private var specName: String? = null
   private var packageName: String? = null
   private var searchScope: TestSearchScope.Wrapper = TestSearchScope.Wrapper()

   fun getTestPath(): String? = testPath
   fun getSpecName(): String? = specName
   fun getPackageName(): String? = packageName

   override fun isPassParentEnvs(): Boolean = passParentEnvs
   override fun isAlternativeJrePathEnabled() = alternativeJrePathEnabled
   override fun getEnvs(): MutableMap<String, String> = envs

   // this is used when something is renamed so the config can update itself
   override fun getRefactoringElementListener(element: PsiElement?): RefactoringElementListener? = null

   override fun getPackage(): String? = null
   override fun getRunClass(): String? = null
   override fun getVMParameters(): String? = vmParameters
   override fun getProgramParameters(): String? = programParameters
   override fun getWorkingDirectory(): String? = workingDirectory

   // used by AbstractJavaTestConfigurationProducer which we don't use as that class is basically
   // around helpers for classes/methods based testing
   override fun getTestType(): String = when {
      testPath != null -> "test"
      specName != null -> "spec"
      packageName != null -> "package"
      else -> "source"
   }

   override fun suggestedName(): String? = RunData(specName, testPath, packageName).suggestedName()

   override fun getConfigurationEditor() = KotestSettingsEditor(this)

   override fun getAlternativeJrePath() = alternativeJrePath?.let { AlternativeJrePathConverter().fromString(it) }
   override fun setAlternativeJrePath(path: String?) {
      val collapsedPath = path?.let { AlternativeJrePathConverter().toString(it) }
      val changed = alternativeJrePath != collapsedPath
      alternativeJrePath = collapsedPath
      ApplicationConfiguration.onAlternativeJreChanged(changed, project)
   }

   override fun setAlternativeJrePathEnabled(enabled: Boolean) {
      val changed = enabled != alternativeJrePathEnabled
      alternativeJrePathEnabled = enabled
      ApplicationConfiguration.onAlternativeJreChanged(changed, project)
   }

   override fun getValidModules(): MutableCollection<Module> {
      // if we are executing a spec (or test in a spec) then we can narrow down the valid modules
      return if (specName != null) {
         JavaRunConfigurationModule.getModulesForClass(project, specName)
      } else {
         ModuleManager.getInstance(project).modules.toMutableList()
      }
   }

   /**
    * Prepares for executing a specific instance of the run configuration.
    *
    * @param executor the execution mode selected by the user (run, debug, profile etc.)
    * @param environment the environment object containing additional settings for executing the configuration.
    *
    * @return the RunProfileState describing the process which is about to be started, or null if it's impossible to start the process.
    */
   override fun getState(executor: Executor, environment: ExecutionEnvironment): KotestRunnableState? {
      return when (configurationModule.module) {
         null -> null
         else -> KotestRunnableState(environment, this)
      }
   }

   override fun canRunOn(target: TargetEnvironmentConfiguration): Boolean {
      return true
   }

   override fun getDefaultLanguageRuntimeType(): LanguageRuntimeType<*>? {
      return null
   }

   override fun getDefaultTargetName(): String? {
      return null
   }

   override fun setDefaultTargetName(targetName: String?) {}

   override fun getTestSearchScope(): TestSearchScope = this.searchScope.scope

   override fun setSearchScope(searchScope: TestSearchScope?) {
      if (searchScope != null) this.searchScope.scope = searchScope
   }

   override fun bePatternConfiguration(classes: MutableList<PsiClass>?, method: PsiMethod?) {}
   override fun beMethodConfiguration(location: Location<PsiMethod>?) {}
   override fun beClassConfiguration(aClass: PsiClass) {}

   override fun setVMParameters(value: String?) {
      vmParameters = value
   }

   override fun setWorkingDirectory(workingDirectory: String?) {
      this.workingDirectory = workingDirectory
   }

   override fun setEnvs(envs: MutableMap<String, String>) {
      this.envs = envs
   }

   fun setTestPath(testName: String?) {
      this.testPath = testName
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

   // used by AbstractJavaTestConfigurationProducer to help when comparing run configuration instances
   // we don't use that abstract class becauase it is mainly focused on class/method style tests
   override fun isConfiguredByElement(element: PsiElement?): Boolean = false

   override fun setProgramParameters(programParameteres: String?) {
      this.programParameters = programParameteres
   }

   /**
    * Returns the text of the context menu action to start this run configuration. This can be different from the run configuration name
    * (for example, for a Java unit test method, the context menu shows just the name of the method, whereas the name of the run
    * configuration includes the class name).
    *
    * @return the name of the action.
    */
   override fun getActionName(): String? = super.getActionName()

   override fun writeExternal(element: Element) {
      super.writeExternal(element)
      JDOMExternalizerUtil.writeField(element, AlternativeJrePathField, alternativeJrePath)
      JDOMExternalizerUtil.writeField(element, AlternativeJrePathEnabledField, alternativeJrePathEnabled.toString())
      JDOMExternalizerUtil.writeField(element, PassParentEnvsField, passParentEnvs.toString())
      JDOMExternalizerUtil.writeField(element, WorkingDirField, workingDirectory)
      JDOMExternalizerUtil.writeField(element, ProgramParamsField, programParameters)
      JDOMExternalizerUtil.writeField(element, SpecNameField, specName)
      JDOMExternalizerUtil.writeField(element, TestPathField, testPath)
      JDOMExternalizerUtil.writeField(element, PackageNameField, packageName)
      JDOMExternalizerUtil.writeField(element, VmParamsField, vmParameters)
      searchScope.writeExternal(element)
      EnvironmentVariablesComponent.writeExternal(element, envs)
   }

   override fun readExternal(element: Element) {
      super.readExternal(element)
      alternativeJrePath = JDOMExternalizerUtil.readField(element, AlternativeJrePathField)
      alternativeJrePathEnabled = JDOMExternalizerUtil.readField(element, AlternativeJrePathEnabledField, "false").toBoolean()
      passParentEnvs = JDOMExternalizerUtil.readField(element, PassParentEnvsField, "false").toBoolean()
      workingDirectory = JDOMExternalizerUtil.readField(element, WorkingDirField)
      programParameters = JDOMExternalizerUtil.readField(element, ProgramParamsField)
      specName = JDOMExternalizerUtil.readField(element, SpecNameField)
      testPath = JDOMExternalizerUtil.readField(element, TestPathField)
      packageName = JDOMExternalizerUtil.readField(element, PackageNameField)
      vmParameters = JDOMExternalizerUtil.readField(element, VmParamsField)
      searchScope.readExternal(element)
      EnvironmentVariablesComponent.readExternal(element, envs)
   }

   override fun createTestConsoleProperties(executor: Executor): SMTRunnerConsoleProperties {
      return KotestTestConsoleProperties(this, executor)
   }

   companion object {
      const val PassParentEnvsField = "passParentEnvs"
      const val ProgramParamsField = "programParameters"
      const val WorkingDirField = "workingDirectory"
      const val PackageNameField = "packageName"
      const val VmParamsField = "vmparams"
      const val TestPathField = "testPath"
      const val SpecNameField = "specName"
      const val AlternativeJrePathField = "jrePath"
      const val AlternativeJrePathEnabledField = "jrePathEnabled"
   }
}
