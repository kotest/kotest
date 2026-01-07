package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.StringArg
import io.kotest.plugin.intellij.psi.enclosingKtClassOrObject
import io.kotest.plugin.intellij.psi.extractStringForStringExtensionFunctonWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringFromStringInvokeWithLambda
import io.kotest.plugin.intellij.psi.extractStringLiteralFromLhsOfInfixFunction
import io.kotest.plugin.intellij.psi.ifMinusOperator
import io.kotest.plugin.intellij.psi.ifCallExpressionLhsStringOpenQuote
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import io.kotest.plugin.intellij.psi.ifOpenQuoteOfLhsArgOfIndexFunction
import io.kotest.plugin.intellij.psi.isDataTestMethodCall
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object WordSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.WordSpec")

   override fun specStyleName(): String = "Word Spec"

   private val fnNames = setOf("should", "Should", "when", "When")

   override fun generateTest(specName: String, name: String): String {
      return "\"$name\" should { }"
   }

   override fun getDataTestMethodNames(): Set<String> =
      setOf(
         "withData",
         "withWhens",
         "withShoulds"
      )

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
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractStringLiteralFromLhsOfInfixFunction(listOf("when", "When"))
      return if (name == null) null else {
         val testName = TestName(null, name.text, name.interpolated)
         Test(testName, null, specClass, TestType.Container, xdisabled = false, psi = this)
      }
   }

   private fun KtBinaryExpression.tryShould(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractStringLiteralFromLhsOfInfixFunction(listOf("should", "Should"))
      return if (name == null) null else {
         val testName = TestName(null, name.text, name.interpolated)
         val w = locateParentWhen()
         return Test(
            name = testName,
            parent = w,
            specClassName = specClass,
            testType = TestType.Container,
            xdisabled = false,
            psi = this
         )
      }
   }

   private fun KtCallExpression.trySubject(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val subject = extractStringFromStringInvokeWithLambda()
      return buildSubjectWithParents(subject, this, specClass)
   }

   private fun KtDotQualifiedExpression.trySubjectWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val subject = extractStringForStringExtensionFunctonWithRhsFinalLambda("config")
      return buildSubjectWithParents(subject, this, specClass)
   }

   private fun buildSubjectWithParents(subject: StringArg?, psi: PsiElement, specClass: KtClassOrObject): Test? {
      return if (subject == null) null else {
         val should = psi.locateParentShould()
         val w = psi.locateParentWhen()
         when {
            should != null && w != null -> Test(
               TestName(null, subject.text, subject.interpolated),
               w,
               specClass,
               TestType.Test,
               xdisabled = false,
               psi = psi
            )
            should != null -> Test(
               TestName(null, subject.text, subject.interpolated),
               should,
               specClass,
               TestType.Test,
               xdisabled = false,
               psi = psi
            )
            else -> Test(
               TestName(null, subject.text, subject.interpolated),
               null,
               specClass,
               TestType.Test,
               xdisabled = false,
               psi = psi
            )
         }
      }
   }

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.trySubject() ?: element.tryDataTest()
         is KtBinaryExpression -> (element.tryShould() ?: element.tryWhen())
         is KtDotQualifiedExpression -> element.trySubjectWithConfig()
         else -> null
      }
   }

   override fun possibleLeafElements(): Set<String> {
      return setOf("OPEN_QUOTE")
   }

   /**
    * For a WordSpec we consider the following scenarios:
    *
    * should("test name") { }
    * xshould("test name") { }
    * should("test name").config(...) {}
    * xshould("test name").config(...) {}
    * when("test name") {}
    * xwhen("test name") {}
    * when("test name").config(...) {}
    * xwhen("test name").config(...) {}
    * withData(...) { }
    * withWhens(...) { }
    * withShoulds(...) { }
    */
   override fun test(element: LeafPsiElement): Test? {
      val ktcall = element.ifCallExpressionLhsStringOpenQuote()
      if (ktcall != null) return test(ktcall)

      val binaryExpression = element.ifOpenQuoteOfLhsArgOfIndexFunction(fnNames)
      if (binaryExpression != null) return test(binaryExpression)

      val ktbinary = element.ifMinusOperator()
      if (ktbinary != null) return test(ktbinary)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      // try to find Data Test Method by finding lambda openings
      val dataMethodCall = element.isDataTestMethodCall(getDataTestMethodNames())
      if (dataMethodCall != null) {
         return test(dataMethodCall)
      }

      return null
   }
}
