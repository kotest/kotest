package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.StringArg
import io.kotest.plugin.intellij.psi.extractStringForStringExtensionFunctonWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringFromStringInvokeWithLambda
import io.kotest.plugin.intellij.psi.extractStringLiteralFromLhsOfInfixFunction
import io.kotest.plugin.intellij.psi.ifBinaryExpressionOperationIdent
import io.kotest.plugin.intellij.psi.ifCallExpressionLhsStringOpenQuote
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
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

   private fun PsiElement.locateParentWhen(): Test? {
      return when (val p = parent) {
         null -> null
         is KtBinaryExpression -> p.tryWhen() ?: parent.locateParentWhen()
         else -> p.locateParentWhen()
      }
   }

   private fun PsiElement.locateParentShould(): Test? {
      return when (val p = parent) {
         null -> null
         is KtBinaryExpression -> p.tryShould() ?: p.locateParentShould()
         else -> p.locateParentShould()
      }
   }

   private fun KtBinaryExpression.tryWhen(): Test? {
      val name = extractStringLiteralFromLhsOfInfixFunction(listOf("when", "When"))
      return if (name == null) null else {
         val testName = TestName(null, name.text, name.interpolated)
         Test(testName, null, TestType.Container, xdisabled = false, psi = this)
      }
   }

   private fun KtBinaryExpression.tryShould(): Test? {
      val name = extractStringLiteralFromLhsOfInfixFunction(listOf("should", "Should"))
      return if (name == null) null else {
         val testName = TestName(null, name.text, name.interpolated)
         val w = locateParentWhen()
         return if (w == null) {
            Test(testName, null, TestType.Container, xdisabled = false, psi = this)
         } else {
            Test(
               testName,
               w,
               TestType.Container,
               xdisabled = false,
               psi = this
            )
         }
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

   private fun buildSubjectWithParents(subject: StringArg?, psi: PsiElement): Test? {
      return if (subject == null) null else {
         val should = psi.locateParentShould()
         val w = psi.locateParentWhen()
         when {
            should != null && w != null -> Test(
               TestName(null, subject.text, subject.interpolated),
               w,
               TestType.Test,
               xdisabled = false,
               psi = psi
            )
            should != null -> Test(
               TestName(null, subject.text, subject.interpolated),
               should,
               TestType.Test,
               xdisabled = false,
               psi = psi
            )
            else -> Test(
               TestName(null, subject.text, subject.interpolated),
               null,
               TestType.Test,
               xdisabled = false,
               psi = psi
            )
         }
      }
   }

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.trySubject()
         is KtBinaryExpression -> (element.tryShould() ?: element.tryWhen())
         is KtDotQualifiedExpression -> element.trySubjectWithConfig()
         else -> null
      }
   }

   override fun possibleLeafElements(): Set<String> {
      return setOf("OPEN_QUOTE")
   }

   override fun test(element: LeafPsiElement): Test? {
      val ktcall = element.ifCallExpressionLhsStringOpenQuote()
      if (ktcall != null) return test(ktcall)

      val ktbinary = element.ifBinaryExpressionOperationIdent()
      if (ktbinary != null) return test(ktbinary)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      return null
   }
}
