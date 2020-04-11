package io.kotest.plugin.intellij

import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.ClassUtil
import com.intellij.psi.util.parentOfType

/**
 * A parser for location URLs reported by test runners.
 * Kotest reports its location hints as kotest://my.package.classname:line
 */
object KotestSMTestLocator : SMTestLocator {

   private const val Protocol = "kotest"

   /**
    * Returns the offset for the given line in this file, or -1 if the document cannot be loaded for this file.
    */
   private fun PsiFile.offsetForLine(line: Int): Int {
      val doc = PsiDocumentManager.getInstance(project).getDocument(this)
      return doc?.getLineStartOffset(line) ?: -1
   }

   private fun loadPsiFile(fqn: String, project: Project, scope: GlobalSearchScope): PsiFile? {
      return ClassUtil.findPsiClass(PsiManager.getInstance(project), fqn, null, true, scope)?.parentOfType<PsiFile>()
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
            val offset = psiFile.offsetForLine(line.toInt())
            val element = psiFile.findElementAt(offset)
            if (element != null) {
               val location: Location<PsiElement> = PsiLocation(project, element)
               list.add(location)
            } else {
               val location: Location<PsiElement> = PsiLocation(project, psiFile)
               list.add(location)
            }
         }
      }
      return list
   }

   override fun getLocationCacheModificationTracker(project: Project): ModificationTracker = ModificationTracker.EVER_CHANGED
}
