package io.kotest.plugin.intellij.ui

import com.intellij.execution.configurations.JavaRunConfigurationModule
import com.intellij.execution.ui.ClassBrowser
import com.intellij.execution.ui.ConfigurationModuleSelector
import com.intellij.ide.util.ClassFilter
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import org.jetbrains.kotlin.idea.search.allScope
import javax.swing.JComponent

class SpecClassBrowser<T : JComponent>(
   project: Project,
   private val moduleSelector: ConfigurationModuleSelector,
) : ClassBrowser<T>(project, KotestBundle.getMessage("spec.class.selector")) {
   override fun getFilter(): ClassFilter.ClassFilterWithScope {
      return object : ClassFilter.ClassFilterWithScope {
         override fun isAccepted(aClass: PsiClass?) =
            aClass?.extendsListTypes?.any { it.name.endsWith("Spec") } ?: false

         override fun getScope() = project.allScope()
      }
   }

   override fun findClass(className: String?): PsiClass? {
      val javaRunConfig = JavaRunConfigurationModule(project, false)
      javaRunConfig.module = moduleSelector.module
      return javaRunConfig.findClass(className)
   }
}
