package io.kotest.plugin.intellij

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
import io.kotest.plugin.intellij.psi.elementAtLine
import io.kotest.plugin.intellij.psi.toPsiLocation

/**
 * A parser for location URLs reported by test runners.
 *
 * Kotest 5.0+ reports its location hints as
 *   kotest:file://filename:linenumber
 * or
 *   kotest:class://fqn:lineNumber
 *
 * Kotest 4 reported its location hints as kotest://class:linenumber
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
         Constants.FileLocatorProtocol -> parseFile(project, scope, path)
         Constants.ClassLocatorProtocol -> parseClass(project, scope, path)
         Constants.OldLocatorProtocol -> parseClass(project, scope, path)
         else -> emptyList()
      }
   }

   override fun getLocationCacheModificationTracker(project: Project): ModificationTracker =
      ModificationTracker.EVER_CHANGED

   private fun parseFile(project: Project, scope: GlobalSearchScope, path: String): List<Location<PsiElement>> {
      val tokens = path.split(':')
      val ident = tokens[0]
      val lineNumber = tokens.getOrNull(1)?.toIntOrNull() ?: 1
      return listOfNotNull(getLocationForFile(project, scope, ident, lineNumber))
   }

   private fun parseClass(project: Project, scope: GlobalSearchScope, path: String): List<Location<PsiElement>> {
      val tokens = path.split(':')
      val ident = tokens[0]
      val lineNumber = tokens.getOrNull(1)?.toIntOrNull() ?: 1
      return listOfNotNull(getLocationForFqn(project, scope, ident, lineNumber))
   }

   /**
    * Returns the PSI file that contains the class indicated by the fully qualified name.
    */
   private fun loadPsiFile(fqn: String, project: Project, scope: GlobalSearchScope): PsiFile? {
      val manager = PsiManager.getInstance(project)
      // this is better than JavaPsiFacade as it will handle inner classes that use $
      val psiClass = ClassUtil.findPsiClass(manager, fqn, null, true, scope)
      val virtualFile = psiClass?.containingFile?.virtualFile
      return virtualFile?.let { manager.findFile(it) }
   }

   private fun getLocationForFqn(
      project: Project,
      scope: GlobalSearchScope,
      fqn: String,
      lineNumber: Int
   ): PsiLocation<PsiElement>? {
      val psiFile = loadPsiFile(fqn, project, scope)
      if (psiFile != null) {
         val element = psiFile.elementAtLine(lineNumber) ?: psiFile
         return element.toPsiLocation()
      }
      return null
   }
}


