package io.kotest.plugin.intellij.annotators

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.enclosingKtClass
import io.kotest.plugin.intellij.psi.isTestFile
import io.kotest.plugin.intellij.psi.specStyle

/**
 * Focus on tests only works at the top level.
 * This annotator will flag as a warning any nested test using f:
 */
class FocusInNestedTestAnnotator : Annotator {
   override fun annotate(element: PsiElement, holder: AnnotationHolder) {
      // we only care about test files
      if (!element.containingFile.isTestFile()) return

      val ktclass = element.enclosingKtClass()
      if (ktclass != null) {
         val style = ktclass.specStyle()
         if (style != null) {
            val test = style.test(element)
            if (test != null) {
               if (test.isFocus && test.isNested) {
                  holder.newAnnotation(HighlightSeverity.WARNING, "Focus only works on top level tests").range(test.psi).create()
               }
            }
         }
      }
   }
}
