package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object FunSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.FunSpec")

   override fun specStyleName(): String = "FunSpec"

   override fun generateTest(specName: String, name: String): String {
      return "test(\"$name\") { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

   /**
    * For a given PsiElement that we know to be a test, we iterate up the stack looking for parent tests.
    */
   private fun locateParentContexts(element: PsiElement): List<String> {
      // if parent is null then we have hit the end
      val parent = element.parent ?: return emptyList()
      val test = when (parent) {
         is KtCallExpression -> parent.tryContext() ?: parent.tryTestWithoutConfig()
         else -> null
      }
      val result = if (test == null) emptyList() else listOf(test)
      return locateParentContexts(parent) + result
   }

   private fun KtCallExpression.tryContext() =
      extractStringArgForFunctionWithStringAndLambdaArgs(listOf("context"))

   /**
    * A test of the form:
    *
    *   test("test name") { }
    *
    */
   private fun KtCallExpression.tryTestWithoutConfig() =
      extractStringArgForFunctionWithStringAndLambdaArgs(listOf("test"))

   /**
    * A test of the form:
    *
    *   test("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryTestWithConfig() =
      this.extractLhsStringArgForDotExpressionWithRhsFinalLambda("test", "config")

   /**
    * For a FunSpec we consider the following scenarios:
    *
    * test("test name") { }
    * test("test name").config(...) {}
    * context("test name").config(...) {}
    */
   override fun testPath(element: PsiElement): String? {
      if (!element.isContainedInSpec()) return null

      val test = when (element) {
         is KtCallExpression -> element.tryContext() ?: element.tryTestWithoutConfig()
         is KtDotQualifiedExpression -> element.tryTestWithConfig()
         else -> null
      }

      return if (test == null) null else {
         val paths = locateParentContexts(element) + test
         return paths.distinct().joinToString(" -- ")
      }
   }

   /**
    * For a FunSpec we consider the following scenarios:
    *
    * test("test name") { }
    * test("test name").config(...) {}
    * context("test name").config(...) {}
    */
   override fun testPath(element: LeafPsiElement): String? {
      if (!element.isContainedInSpec()) return null

      val ktcall = element.ifCallExpressionNameIdent()
      if (ktcall != null) return testPath(ktcall)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return testPath(ktdot)

      return null
   }
}

data class Test(val name: String, val path: String, val enabled: Boolean) {
   constructor(name: String, path: String) : this(name, path, !name.startsWith("!"))
}

data class TestElement(val psi: PsiElement,
                       val test: Test,
                       val tests: List<TestElement>)
