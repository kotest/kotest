package io.kotest.plugin.intellij

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testIntegration.TestFinder
import io.kotest.plugin.intellij.psi.enclosingSpec
import io.kotest.plugin.intellij.psi.isContainedInSpec

/**
 * Triggered when the user uses the "Navigation / Test"  action (= jump to test shortcut).
 * Corresponding extension point qualified name is {@code com.intellij.testFinder}.
 */
class KotestTestFinder : TestFinder {

   /**
    * Retrieve the source element (PsiFile) to handle some UI elements, like the name displayed in "Choose Test for {file name}".
    *
    * @param from PsiElement where the cursor was when "Navigate to test" was triggered
    * @return the parent PsiFile of the PsiElement where the cursor was when the test finder was invoked
    */
   override fun findSourceElement(from: PsiElement): PsiElement? = from.enclosingSpec()

   // handled by the JavaTestFinder impl
   /**
    * Finds tests for given class.
    *
    * @param element may by of any language but not specific to a current test finder domain language
    * @return found tests for class
    */
   override fun findTestsForClass(element: PsiElement): MutableCollection<PsiElement> = mutableListOf()

   override fun findClassesForTest(element: PsiElement): MutableCollection<PsiElement> {
      val spec = element.enclosingSpec() ?: return mutableListOf()
      val scope = GlobalSearchScope.allScope(element.project)
      val potentialName = spec.name?.removeSuffix("Test")?.removeSuffix("Spec") ?: return mutableListOf()
      return JavaPsiFacade.getInstance(element.project).findClasses(potentialName, scope).asList().toMutableList()
   }

   /**
    * This is used by the navigation menu to determine if it should show "navigate to tests" or
    * "navigate to test subjects". Depending on the response, [findClassesForTest] or
    * [findTestsForClass] will be called.
    */
   override fun isTest(element: PsiElement): Boolean = element.isContainedInSpec()
}
