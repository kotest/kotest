package io.kotest.plugin.intellij.run.idea

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ModuleBasedConfiguration
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Ref
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PackageScope
import com.intellij.psi.search.searches.ClassInheritorsSearch
import io.kotest.plugin.intellij.run.RunnerMode
import io.kotest.plugin.intellij.run.RunnerModes
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration

@Suppress("DEPRECATION")
@Deprecated("Starting with Kotest 6.1 use GradleMultiplatformJvmTestTaskRunProducer")
class PackageRunConfigurationProducer : LazyRunConfigurationProducer<KotestRunConfiguration>() {

   /**
    * Returns the [KotestConfigurationFactory] used to create [KotestRunConfiguration]s.
    */
   override fun getConfigurationFactory(): ConfigurationFactory = KotestConfigurationFactory(KotestConfigurationType())

   /**
    * When two configurations are created from the same context by two different producers, checks if the
    * configuration created by this producer should be preferred over the other one.
    *
    * We return true when the other configuration is NOT a Gradle configuration and NOT another package-level
    * Kotest configuration. This ensures the package config takes priority over spec or test-level Kotest
    * configurations (which should not win when the run was triggered from a package element), as well as
    * over JUnit configurations.
    *
    * We do NOT prefer this IDEA-based runner over Gradle configurations, as Gradle is the preferred method
    * for running Kotest tests starting with Kotest 6.
    */
   override fun isPreferredConfiguration(self: ConfigurationFromContext?, other: ConfigurationFromContext?): Boolean {
      val otherConfig = other?.configuration
      // Don't prefer over Gradle configurations
      if (otherConfig is GradleRunConfiguration) return false
      // Don't prefer over another package-level Kotest configuration
      if (otherConfig is KotestRunConfiguration && otherConfig.getPackageName() != null) return false
      // Prefer this package config over spec/test-level Kotest configurations (like JUnit)
      return true
   }

   /**
    * Returns true if this configuration should replace the other configuration.
    * We replace JUnit configurations and spec/test-level Kotest configurations when running from a package
    * element, but NOT Gradle configurations or other package-level Kotest configurations.
    */
   override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      val otherConfig = other.configuration
      // Don't replace Gradle configurations
      if (otherConfig is GradleRunConfiguration) return false
      // Don't replace another package-level Kotest configuration
      if (otherConfig is KotestRunConfiguration && otherConfig.getPackageName() != null) return false
      // Replace spec/test-level Kotest configurations and JUnit configurations with this package config
      return true
   }

   override fun isConfigurationFromContext(
      configuration: KotestRunConfiguration,
      context: ConfigurationContext
   ): Boolean = false

   override fun setupConfigurationFromContext(
      configuration: KotestRunConfiguration,
      context: ConfigurationContext,
      sourceElement: Ref<PsiElement>
   ): Boolean {

      if (RunnerModes.mode(context.module) != RunnerMode.LEGACY) return false

      val index = ProjectRootManager.getInstance(context.project).fileIndex
      val dirservice = JavaDirectoryService.getInstance()
      val psiDirectory = sourceElement.get()
      if (psiDirectory is PsiJavaDirectoryImpl) {
         if (index.isInTestSourceContent(psiDirectory.virtualFile)) {
            val psiPackage = dirservice.getPackage(psiDirectory)
            if (psiPackage != null) {
               val psiClasses = findKotestSpecsByStyle(context.project, psiPackage.qualifiedName);
               val specs = psiClasses.joinToString(";") { it.qualifiedName.toString() }
               LOG.info("Found ${psiClasses.size} classes in package ${psiPackage.qualifiedName}")
               setupConfigurationModule(context, configuration)
               configuration.setPackageName(psiPackage.qualifiedName)
               configuration.setSpecsName(specs)
               configuration.name = generateName(psiPackage.qualifiedName)
               return true
            }
         }
      }
      return false
   }

   fun findKotestSpecsByStyle(
      project: Project,
      targetPackageName: String
   ): List<PsiClass> {
      val kotestStyles = SpecStyle.styles.map { it.fqn().asString() }.toSet()
      val facade = JavaPsiFacade.getInstance(project)
      val targetPackage = facade.findPackage(targetPackageName) ?: return emptyList()
      val packageScope = PackageScope(targetPackage, true, false)
      val libraryScope = GlobalSearchScope.allScope(project)
      val foundClasses = mutableSetOf<PsiClass>() as LinkedHashSet<PsiClass>
      for (styleFqn in kotestStyles) {
         val styleClass = facade.findClass(styleFqn, libraryScope) ?: continue
         val query = ClassInheritorsSearch.search(styleClass, packageScope, true)
         foundClasses.addAll(query.findAll())
      }
      return foundClasses.filter { it.language.id == "kotlin" }
   }

   private fun setupConfigurationModule(context: ConfigurationContext, configuration: KotestRunConfiguration): Boolean {
      val template = context.runManager.getConfigurationTemplate(configurationFactory)
      val contextModule = context.module
      val predefinedModule = (template.configuration as ModuleBasedConfiguration<*, *>).configurationModule.module
      if (predefinedModule != null) {
         configuration.setModule(predefinedModule)
         return true
      }
      val module = findModule(configuration, contextModule)
      if (module != null) {
         configuration.setModule(module)
         return true
      }
      return false
   }

   private fun findModule(configuration: KotestRunConfiguration, contextModule: Module?): Module? {
      if (configuration.configurationModule.module == null && contextModule != null) {
         return contextModule
      }
      return null
   }

   companion object {
      private val LOG = Logger.getInstance(PackageRunConfigurationProducer::class.java)
   }
}
