@file:Suppress("UnstableApiUsage")

package io.kotest.plugin.intellij.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import io.kotest.plugin.intellij.AbstractInspection
import org.jetbrains.kotlin.idea.intentions.loopToCallChain.isTrueConstant
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression

class ShouldBeInstanceOfInspection : AbstractInspection() {
   override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
      return object : PsiElementVisitor() {
         override fun visitElement(element: PsiElement) {
            if (element is KtBinaryExpression) {
               if (element.operationReference.text == "shouldBe") {
                  if (element.right?.isTrueConstant() == true) {
                     val parenthesizedExpression = element.left
                     if (parenthesizedExpression is KtParenthesizedExpression) {
                        if (parenthesizedExpression.expression is KtIsExpression) {
                           holder.registerProblem(
                              element,
                              "Replace with shouldBeInstanceOf",
                              ProblemHighlightType.WARNING,
                              ShouldBeInstanceOfQuickFix(element)
                           )
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
