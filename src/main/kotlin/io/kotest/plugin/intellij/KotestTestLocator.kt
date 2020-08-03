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
import io.kotest.plugin.intellij.psi.elementAtLine

/**
 * A parser for location URLs reported by test runners.
 * Kotest reports its location hints as kotest://my.package.classname:line
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

   override fun getLocation(protocol: String,
                            path: String,
                            project: Project,
                            scope: GlobalSearchScope): List<Location<PsiElement>> {
      val list = mutableListOf<Location<PsiElement>>()
      if (protocol == Protocol) {
         val (fqn, line) = path.split(':')
         val psiFile = loadPsiFile(fqn, project, scope)
         if (psiFile != null) {
            val element = psiFile.elementAtLine(line.toInt()) ?: psiFile
            val location: Location<PsiElement> = PsiLocation(project, element)
            list.add(location)
         }
      }
      return list
   }

   override fun getLocationCacheModificationTracker(project: Project): ModificationTracker = ModificationTracker.EVER_CHANGED
}


