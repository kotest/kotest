package io.kotest.plugin.intellij

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testIntegration.TestFinder
import io.kotest.plugin.intellij.psi.enclosingSpec
import io.kotest.plugin.intellij.psi.isContainedInSpec

class KotestTestFinder : TestFinder {

   override fun findSourceElement(from: PsiElement): PsiElement? = from.enclosingSpec()

   // handled by the JavaTestFinder impl
   override fun findTestsForClass(element: PsiElement): MutableCollection<PsiElement> = mutableListOf()

   override fun findClassesForTest(element: PsiElement): MutableCollection<PsiElement> {
      val spec = element.enclosingSpec() ?: return mutableListOf()
      val scope = GlobalSearchScope.allScope(element.project)
      val potentialName = spec.name?.removeSuffix("Test")?.removeSuffix("Spec") ?: return mutableListOf()
      return JavaPsiFacade.getInstance(element.project).findClasses(potentialName, scope).asList().toMutableList()
   }

   /**
    * This is used by the navivation menu to determine if it should show "navigate to tests" or
    * "nagivate to test subjects". Depending on the response, [findClassesForTest] or
    * [findTestsForClass] will be called.
    */
   override fun isTest(element: PsiElement): Boolean = element.isContainedInSpec()
}
