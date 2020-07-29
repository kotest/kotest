package io.kotest.plugin.intellij.annotators

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.createWarnAnnotation
import io.kotest.plugin.intellij.psi.enclosingKtClass
import io.kotest.plugin.intellij.psi.isTestFile
import io.kotest.plugin.intellij.psi.specStyle

class DuplicatedTestNameAnnotator : Annotator {
   override fun annotate(element: PsiElement, holder: AnnotationHolder) {
      // we only care about test files
      if (!element.containingFile.isTestFile()) return

      val ktclass = element.enclosingKtClass()
      if (ktclass != null) {
         val style = ktclass.specStyle()
         if (style != null) {
            val test = style.test(element)
            // if the name is interpolated we can't run checks as it could be anything
            if (test != null && !test.name.interpolated) {
               val tests = style.tests(ktclass)
               val duplicated = tests.count { it.test.name == test.name } > 1
               if (duplicated) {
                  holder.createWarnAnnotation(test.psi, "Duplicated test name")
               }
            }
         }
      }
   }
}
