package io.kotest.plugin.intellij.annotators

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.enclosingKtClass
import io.kotest.plugin.intellij.psi.specStyle

class DuplicatedTestNameAnnotator : Annotator {
   override fun annotate(element: PsiElement, holder: AnnotationHolder) {

      // we only care about test files
      val vfile = element.containingFile.virtualFile
      if (!ProjectRootManager.getInstance(element.project).fileIndex.isInTestSourceContent(vfile))
         return

      // only change when the test itself has been modified
      val ktclass = element.enclosingKtClass()
      if (ktclass != null) {
         val style = ktclass.specStyle()
         if (style != null) {
            val test = style.test(element)
            if (test != null) {
               val tests = style.tests(ktclass)
               val duplicated = tests.count { it.test.name == test.name } > 1
               if (duplicated) {
                  val range = test.psi.textRange
                  holder.createErrorAnnotation(range, "Duplicated test name")
               }
            }
         }
      }
   }
}
