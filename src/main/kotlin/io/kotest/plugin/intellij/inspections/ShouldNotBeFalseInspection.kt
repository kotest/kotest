@file:Suppress("UnstableApiUsage")

package io.kotest.plugin.intellij.inspections

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import io.kotest.plugin.intellij.psi.isTestFile
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.idea.intentions.loopToCallChain.isFalseConstant
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class ShouldNotBeFalseQuickFix(element: KtBinaryExpression) : LocalQuickFixOnPsiElement(element) {

   override fun getFamilyName(): String = "Replace with shouldBe true"
   override fun getText(): String = "Replace with shouldBe true"

   override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
      val psiFactory = KtPsiFactory(project)
      val right = psiFactory.createExpression("true")
      val op = psiFactory.createOperationName("shouldBe")
      (startElement as KtBinaryExpression).right?.replace(right)
      startElement.operationReference.replace(op)
   }
}

class ShouldNotBeFalseInspection : AbstractKotlinInspection() {
   override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
      return object : PsiElementVisitor() {
         override fun visitElement(element: PsiElement) {
            if (!element.containingFile.isTestFile()) return

            if (element is KtBinaryExpression) {
               if (element.operationReference.text == "shouldNotBe") {
                  if (element.right?.isFalseConstant() == true) {
                     holder.registerProblem(
                        element,
                        "Replace with shouldBe true",
                        ProblemHighlightType.WARNING,
                        ShouldNotBeFalseQuickFix(element)
                     )
                  }
               }
            }
         }
      }
   }
}
