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
import io.kotest.plugin.intellij.dependencies.ModuleDependencies
import io.kotest.plugin.intellij.styles.SpecStyle
import io.kotest.plugin.intellij.run.idea.KotestRunConfiguration
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration


@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
class PackageRunConfigurationProducer : LazyRunConfigurationProducer<KotestRunConfiguration>() {

   /**
    * Returns the [KotestConfigurationFactory] used to create [KotestRunConfiguration]s.
    */
   override fun getConfigurationFactory(): ConfigurationFactory = KotestConfigurationFactory(KotestConfigurationType())

   /**
    * When two configurations are created from the same context by two different producers, checks if the
    * configuration created by this producer should be preferred over the other one.
    *
    * We return true when the other configuration is NOT a Kotest configuration and NOT a Gradle configuration,
    * to ensure Kotest specs take priority over JUnit (which may claim the class due to Spring Boot test annotations
    * like `@SpringBootTest` that are meta-annotated with `@ExtendWith(SpringExtension.class)`).
    *
    * We do NOT prefer this IDEA-based runner over Gradle configurations, as Gradle is the preferred method
    * for running Kotest tests starting with Kotest 6.
    */
   override fun isPreferredConfiguration(self: ConfigurationFromContext?, other: ConfigurationFromContext?): Boolean {
      val otherConfig = other?.configuration
      // Don't prefer over Gradle or other Kotest configurations
      if (otherConfig is GradleRunConfiguration || otherConfig is KotestRunConfiguration) return false
      // Prefer this over non-Kotest, non-Gradle configurations (like JUnit)
      return true
   }

   /**
    * Returns true if this configuration should replace the other configuration.
    * We replace JUnit configurations when we detect a Kotest spec, but NOT Gradle configurations.
    */
   override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      val otherConfig = other.configuration
      // Don't replace Gradle or other Kotest configurations
      if (otherConfig is GradleRunConfiguration || otherConfig is KotestRunConfiguration) return false
      // Replace non-Kotest, non-Gradle configurations (like JUnit) with Kotest
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

      // if we don't have the kotest engine on the classpath then we shouldn't use this producer
      if (!ModuleDependencies.hasKotest(context.module)) return false

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
               LOG.info(
                  """
                  Specs:
                     $specs
               """.trimIndent()
               )
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
