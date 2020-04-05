package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object WordSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.WordSpec")

   override fun specStyleName(): String = "WordSpec"

   override fun generateTest(specName: String, name: String): String {
      return "\"$name\" should { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

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

   override fun testPath(element: PsiElement): String? {
      if (!element.isContainedInSpec()) return null

      return when (element) {
         is KtCallExpression -> element.trySubject()?.path
         is KtBinaryExpression -> (element.tryShould() ?: element.tryWhen())?.path
         is KtDotQualifiedExpression -> element.trySubjectWithConfig()?.path
         else -> null
      }
   }

   override fun testPath(element: LeafPsiElement): String? {
      if (!element.isContainedInSpec()) return null

      val ktcall = element.ifCallExpressionLhsStringOpenQuote()
      if (ktcall != null) return testPath(ktcall)

      val ktbinary = element.ifBinaryExpressionOperationIdent()
      if (ktbinary != null) return testPath(ktbinary)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return testPath(ktdot)

      return null
   }
}
