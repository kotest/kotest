package io.kotest.plugin.intellij.tests

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testIntegration.TestFinder
import io.kotest.plugin.intellij.psi.enclosingSpec
import io.kotest.plugin.intellij.psi.isContainedInSpecEdt

/**
 * Triggered when the user uses the "Navigation / Test" or "Navigation / Test Subject" menu action (= jump to test shortcut).
 * Corresponding extension point qualified name is {@code com.intellij.testFinder}.
 *
 * When the user opens the navigate window, the [isTest] method is called to determine if this is a test, or a test subject.
 * If it's a test, then [findClassesForTest] is called to find the test subject.
 * If it's a test subject, then [findTestsForClass] is called to find the tests.
 */
class KotestTestFinder : TestFinder {

   /**
    * Retrieve the source element (PsiFile) to handle some UI elements, like the name displayed in "Choose Test for {file name}".
    *
    * @param from PsiElement where the cursor was when "Navigate to test" was triggered
    * @return the parent PsiFile of the PsiElement where the cursor was when the test finder was invoked
    */
   override fun findSourceElement(from: PsiElement): PsiElement? = from.enclosingSpec()

   /**
    * Finds tests for given class.
    *
    * Since the logic for kotest is basically the same as for JUnit, we just let the
    * JavaTestFinder implementation handle it.
    *
    * @param element may be any language but not specific to a current test finder domain language
    * @return applicable tests for class
    */
   override fun findTestsForClass(element: PsiElement): MutableCollection<PsiElement> {
      return mutableListOf()
   }

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
   override fun isTest(element: PsiElement): Boolean {
      return element.isContainedInSpecEdt()
   }
}
