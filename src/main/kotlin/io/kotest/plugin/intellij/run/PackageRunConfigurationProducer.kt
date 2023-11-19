package io.kotest.plugin.intellij.run

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ModuleBasedConfiguration
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Ref
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl
import io.kotest.plugin.intellij.KotestRunConfiguration
import io.kotest.plugin.intellij.KotestConfigurationFactory
import io.kotest.plugin.intellij.KotestConfigurationType

class PackageRunConfigurationProducer : LazyRunConfigurationProducer<KotestRunConfiguration>() {

   /**
    * Returns the [KotestConfigurationFactory] used to create [KotestRunConfiguration]s.
    */
   override fun getConfigurationFactory(): ConfigurationFactory = KotestConfigurationFactory(KotestConfigurationType())

   override fun isConfigurationFromContext(configuration: KotestRunConfiguration, context: ConfigurationContext): Boolean = false

   override fun setupConfigurationFromContext(configuration: KotestRunConfiguration,
                                              context: ConfigurationContext,
                                              sourceElement: Ref<PsiElement>): Boolean {
      val index = ProjectRootManager.getInstance(context.project).fileIndex
      val dirservice = JavaDirectoryService.getInstance()
      val psiDirectory = sourceElement.get()
      if (psiDirectory is PsiJavaDirectoryImpl) {
         if (index.isInTestSourceContent(psiDirectory.virtualFile)) {
            val psiPackage = dirservice.getPackage(psiDirectory)
            if (psiPackage != null) {
               setupConfigurationModule(context, configuration)
               configuration.setPackageName(psiPackage.qualifiedName)
               configuration.name = generateName(psiPackage.qualifiedName)
               return true
            }
         }
      }
      return false
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
}
