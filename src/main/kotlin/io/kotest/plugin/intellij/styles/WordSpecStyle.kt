package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.extractStringForStringExtensionFunctonWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringFromStringInvokeWithLambda
import io.kotest.plugin.intellij.psi.extractStringLiteralFromLhsOfInfixFunction
import io.kotest.plugin.intellij.psi.ifBinaryExpressionOperationIdent
import io.kotest.plugin.intellij.psi.ifCallExpressionLhsStringOpenQuote
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import io.kotest.plugin.intellij.psi.matchInfixFunctionWithStringAndLambaArg
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object WordSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.WordSpec")

   override fun specStyleName(): String = "Word Spec"

   override fun generateTest(specName: String, name: String): String {
      return "\"$name\" should { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private fun PsiElement.locateParentWhen(): String? {
      val wen = this.matchInfixFunctionWithStringAndLambaArg(listOf("when", "When"))
      return when {
         wen != null -> wen
         parent != null -> parent.locateParentWhen()
         else -> null
      }
   }

   private fun PsiElement.locateParentShould(): String? {
      val should = this.matchInfixFunctionWithStringAndLambaArg(listOf("should", "Should"))
      return when {
         should == null && parent == null -> null
         should == null -> parent.locateParentShould()
         else -> should
      }
   }

   private fun KtBinaryExpression.tryWhen(): Test? {
      val name = extractStringLiteralFromLhsOfInfixFunction(listOf("When"))
      return if (name == null) null else Test(name, name)
   }

   private fun KtBinaryExpression.tryShould(): Test? {
      val name = extractStringLiteralFromLhsOfInfixFunction(listOf("should", "Should"))
      return if (name == null) null else {
         val w = parent.locateParentWhen()
         return if (w == null) Test(name, name) else Test(name, "$w when $name")
      }
   }

   private fun KtCallExpression.trySubject(): Test? {
      val subject = extractStringFromStringInvokeWithLambda()
      return buildSubjectWithParents(subject, this)
   }

   private fun KtDotQualifiedExpression.trySubjectWithConfig(): Test? {
      val subject = extractStringForStringExtensionFunctonWithRhsFinalLambda("config")
      return buildSubjectWithParents(subject, this)
   }

   private fun buildSubjectWithParents(subject: String?, psi: PsiElement): Test? {
      return if (subject == null) null else {
         val should = psi.locateParentShould()
         val w = psi.locateParentWhen()
         when {
            should == null && w == null -> Test(subject, subject)
            w == null -> Test(subject, "$should should $subject")
            else -> Test(subject, "$w when $should should $subject")
         }
      }
   }

   override fun test(element: PsiElement): Test? {
      if (!element.isContainedInSpec()) return null

      return when (element) {
         is KtCallExpression -> element.trySubject()
         is KtBinaryExpression -> (element.tryShould() ?: element.tryWhen())
         is KtDotQualifiedExpression -> element.trySubjectWithConfig()
         else -> null
      }
   }

   override fun test(element: LeafPsiElement): Test? {
      if (!element.isContainedInSpec()) return null

      val ktcall = element.ifCallExpressionLhsStringOpenQuote()
      if (ktcall != null) return test(ktcall)

      val ktbinary = element.ifBinaryExpressionOperationIdent()
      if (ktbinary != null) return test(ktbinary)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      return null
   }
}
