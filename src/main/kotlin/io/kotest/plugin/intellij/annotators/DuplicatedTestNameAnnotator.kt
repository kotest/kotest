package io.kotest.plugin.intellij.annotators

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.createWarnAnnotation
import io.kotest.plugin.intellij.psi.enclosingKtClass
import io.kotest.plugin.intellij.psi.isTestFile
import io.kotest.plugin.intellij.psi.specStyle

/**
 * Marks as a warning annotation any duplicated test names in a test file.
 */
class DuplicatedTestNameAnnotator : Annotator {

   override fun annotate(element: PsiElement, holder: AnnotationHolder) {
      // we only care about test files
      if (!element.containingFile.isTestFile()) return

      val ktclass = element.enclosingKtClass() ?: return
      val style = ktclass.specStyle() ?: return

      // returns a test description if this element is the anchor for a test
      val test = style.test(element) ?: return

      // if the name is interpolated we can't run checks as we don't know the runtime name
      if (test.name.interpolated) return

      // locate all tests for this style
      val tests = style.tests(ktclass, false)

      // generate the full path as nested tests may be unique if inside differently named parents
      // this test is duplicated if any other test has the same full path
      val duplicated = tests.count { it.test.descriptorPath() == test.descriptorPath() } > 1

      if (duplicated) {
         holder.createWarnAnnotation(test.psi, "Duplicated test name")
      }
   }
}
