package io.kotest.plugin.intellij.locations

import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.ClassUtil
import io.kotest.plugin.intellij.Constants
import io.kotest.plugin.intellij.psi.elementAtLine

/**
 * A parser for location URLs reported by test runners.
 *
 * Kotest reports its location hints as kotest://qualifiedName:linenumber
 */
class KotestTestLocator : SMTestLocator, DumbAware {

   override fun getLocation(
      protocol: String,
      path: String,
      project: Project,
      scope: GlobalSearchScope
   ): List<Location<PsiElement>> {
      return getLocation(protocol, path, null, project, scope)
   }

   override fun getLocation(
      protocol: String,
      path: String,
      metainfo: String?,
      project: Project,
      scope: GlobalSearchScope
   ): List<Location<PsiElement>> {
      return when (protocol) {
         Constants.KOTEST_CLASS_LOCATOR_PROTOCOL -> parseClass(project, scope, path)
         else -> emptyList()
      }
   }

   override fun getLocationCacheModificationTracker(project: Project): ModificationTracker =
      ModificationTracker.EVER_CHANGED

   private fun parseClass(project: Project, scope: GlobalSearchScope, path: String): List<Location<PsiElement>> {
      val tokens = path.split(':')
      val qualifiedName = tokens[0]
      val lineNumber = tokens.getOrNull(1)?.toIntOrNull()
      return listOfNotNull(getLocationForFqn(project, scope, qualifiedName, lineNumber))
   }

   private fun getLocationForFqn(
      project: Project,
      scope: GlobalSearchScope,
      fqn: String,
      lineNumber: Int?,
   ): PsiLocation<PsiElement>? {

      val psiFile = loadPsiFile(fqn, project, scope) ?: return null
      if (lineNumber == null) return PsiLocation(project, psiFile)

      val element = psiFile.elementAtLine(lineNumber)
      return if (element == null) PsiLocation(project, psiFile) else PsiLocation(project, element)
   }

   /**
    * Returns the PSI file that contains the class indicated by the qualified name.
    */
   private fun loadPsiFile(qualifiedName: String, project: Project, scope: GlobalSearchScope): PsiFile? {
      val manager = PsiManager.getInstance(project)
      // this is better than JavaPsiFacade as it will handle inner classes that use $
      val psiClass = ClassUtil.findPsiClass(manager, qualifiedName, null, true, scope)
      val virtualFile = psiClass?.containingFile?.virtualFile
      return virtualFile?.let { manager.findFile(it) }
   }
}
