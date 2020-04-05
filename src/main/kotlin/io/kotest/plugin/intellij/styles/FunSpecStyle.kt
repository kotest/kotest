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

   /**
    * Returns all child tests located in the given [PsiElement].
    */
   override fun tests(element: PsiElement): List<TestElement> {
      return element.children.flatMap { child ->
         val childTests = tests(child)
         val testPath = testPath(child)
         if (testPath != null) {
            listOf(TestElement(child, Test(testPath, testPath), childTests))
         } else childTests
      }
   }

   private fun KtCallExpression.tryContext() =
      extractStringArgForFunction2WithStringAndLambda(listOf("context"))

   /**
    * A test of the form:
    *
    *   test("test name") { }
    *
    */
   private fun KtCallExpression.tryTestWithoutConfig() =
      extractStringArgForFunction2WithStringAndLambda(listOf("test"))

   /**
    * A test of the form:
    *
    *   test("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryTestWithConfig() =
      this.extractLhsStringArgForDotExpressionWithRhsFinalLambda("test", "config")

   /**
    * Finds tests of the form:
    *
    *   test("test name") { }
    */
   private fun extractTestWithoutConfig(element: LeafPsiElement): String? {
      return element.ifCallExpressionName()?.tryTestWithoutConfig()
   }

   /**
    * Finds tests of the form:
    *
    *   test("test name").config(...) { }
    */
   private fun extractTestWithConfig(element: LeafPsiElement): String? {
      return element.ifDotExpressionSeparator()?.tryTestWithConfig()
   }

   /**
    * Finds tests of the form:
    *
    *   context("test name") {}
    */
   private fun extractContext(element: LeafPsiElement): String? {
      return element.ifCallExpressionName()?.tryContext()
   }

   /**
    * Returns the test path for a given [LeafPsiElement],
    * or if this element is not a test, then returns null.
    *
    * For a FunSpec we consider the following scenarios:
    *
    * test("test name") { }
    * test("test name").config(...) {}
    */
   override fun testPath2(element: LeafPsiElement): String? {
      if (!element.isContainedInSpec()) return null

      val test = extractTestWithoutConfig(element)
         ?: extractTestWithConfig(element)
         ?: extractContext(element)
         ?: return null

      val paths = locateParentContexts(element) + test
      return paths.distinct().joinToString(" -- ")
   }
}

data class Test(val name: String, val path: String, val enabled: Boolean) {
   constructor(name: String, path: String) : this(name, path, !name.startsWith("!"))
}

data class TestElement(val psi: PsiElement,
                       val test: Test,
                       val tests: List<TestElement>)
