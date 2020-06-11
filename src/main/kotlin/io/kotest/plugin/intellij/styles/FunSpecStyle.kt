package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.extractLhsStringArgForDotExpressionWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringArgForFunctionWithStringAndLambdaArgs
import io.kotest.plugin.intellij.psi.ifCallExpressionLambdaOpenBrace
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object FunSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.FunSpec")

   override fun specStyleName(): String = "Fun Spec"

   override fun generateTest(specName: String, name: String): String {
      return "test(\"$name\") { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private fun locateParentTests(element: PsiElement): List<Test> {
      // if parent is null then we have hit the end
      val p = element.parent ?: return emptyList()
      val context = if (p is KtCallExpression) listOfNotNull(p.tryContext()) else emptyList()
      return locateParentTests(p) + context
   }

   private fun KtCallExpression.tryContext(): Test? {
      val context = extractStringArgForFunctionWithStringAndLambdaArgs("context") ?: return null
      return buildTest(context, this, TestType.Container)
   }

   /**
    * A test of the form:
    *
    *   test("test name") { }
    *
    */
   private fun KtCallExpression.tryTest(): Test? {
      val expect = extractStringArgForFunctionWithStringAndLambdaArgs("test") ?: return null
      return buildTest(expect, this, TestType.Test)
   }

   /**
    * A test of the form:
    *
    *   test("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryTestWithConfig(): Test? {
      val expect = extractLhsStringArgForDotExpressionWithRhsFinalLambda("test", "config") ?: return null
      return buildTest(expect, this, TestType.Test)
   }

   private fun buildTest(testName: String, element: PsiElement, type: TestType): Test {
      val contexts = locateParentTests(element)
      val path = (contexts.map { it.name } + testName).joinToString(" ")
      return Test(testName, path, type, element)
   }

   /**
    * For a FunSpec we consider the following scenarios:
    *
    * test("test name") { }
    * test("test name").config(...) {}
    * context("test name").config(...) {}
    */
   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.tryContext() ?: element.tryTest()
         is KtDotQualifiedExpression -> element.tryTestWithConfig()
         else -> null
      }
   }

   /**
    * For a FunSpec we consider the following scenarios:
    *
    * test("test name") { }
    * test("test name").config(...) {}
    * context("test name").config(...) {}
    */
   override fun test(element: LeafPsiElement): Test? {
      val ktcall = element.ifCallExpressionLambdaOpenBrace()
      if (ktcall != null) return test(ktcall)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      return null
   }
}

enum class TestType {
   Container, Test
}

data class Test(val name: String, val path: String, val enabled: Boolean, val testType: TestType, val psi: PsiElement) {
   constructor(name: String, path: String, testType: TestType, psi: PsiElement) : this(name, path, !name.startsWith("!"), testType, psi)
}

data class TestElement(val psi: PsiElement,
                       val test: Test,
                       val tests: List<TestElement>)
