package io.kotest.plugin.intellij

import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.ClassUtil
import com.intellij.util.containers.addIfNotNull
import io.kotest.plugin.intellij.psi.elementAtLine

/**
 * A parser for location URLs reported by test runners.
 * Kotest reports its location hints as kotest://filename:line
 */
object KotestTestLocator : SMTestLocator {

   private const val Protocol = Constants.LocatorProtocol

   /**
    * Returns the PSI file that contains the class indicated by the fully qualified name.
    */
   private fun loadPsiFile(fqn: String, project: Project, scope: GlobalSearchScope): PsiFile? {
      val manager = PsiManager.getInstance(project)
      val lightClass = ClassUtil.findPsiClass(manager, fqn, null, true, scope)
      val virtualFile = lightClass?.containingFile?.virtualFile
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
         return PsiLocation(project, element)
      }
      return null
   }

   override fun getLocation(
      protocol: String,
      path: String,
      project: Project,
      scope: GlobalSearchScope
   ): List<Location<PsiElement>> {
      val list = mutableListOf<Location<PsiElement>>()
      if (protocol == Protocol) {
         val tokens = path.split(':')
         val ident = tokens[0]
         val lineNumber = tokens.getOrNull(1)?.toIntOrNull()
         if (lineNumber != null) {
            val location = when {
               ident.startsWith("class/") -> getLocationForFqn(project, scope, ident.removePrefix("class/"), lineNumber)
               ident.startsWith("file/") -> getLocationForFqn(project, scope, ident.removePrefix("file/"), lineNumber)
               else -> getLocationForFqn(project, scope, ident, lineNumber)
            }
            list.addIfNotNull(location)
         }
      }
      return list
   }

   override fun getLocationCacheModificationTracker(project: Project): ModificationTracker =
      ModificationTracker.EVER_CHANGED
}


