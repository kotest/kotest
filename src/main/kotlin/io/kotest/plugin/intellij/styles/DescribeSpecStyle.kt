package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.ifType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object DescribeSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.DescribeSpec")

   override fun specStyleName(): String = "DescribeSpec"

   override fun generateTest(specName: String, name: String): String {
      return "describe(\"$name\") { }"
   }

   // todo this could be optimized to not check for the other parts of the tree until the name is needed
   override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

   /**
    * For a given PsiElement that we know to be a test, we iterate up the stack looking for parent tests.
    */
   private fun locateParentContexts(element: PsiElement): List<String> {
      // if parent is null then we have hit the end
      val parent = element.parent ?: return emptyList()
      val test = when (parent) {
         is KtCallExpression -> parent.tryDescribe() ?: parent.tryXDescribe() ?: parent.tryContext()
         else -> null
      }
      val result = if (test == null) emptyList() else listOf(test)
      return locateParentContexts(parent) + result
   }

   /**
    * Finds tests in the form:
    *
    *   describe("test name") { }
    *
    */
   private fun KtCallExpression.tryDescribe(): String? {
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("describe")
      return if (name == null) null else "Describe: $name"
   }

   /**
    * Finds tests in the form:
    *
    *   context("test name") { }
    *
    */
   private fun KtCallExpression.tryContext(): String? {
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("context")
      return if (name == null) null else "Context: $name"
   }

   /**
    * Finds tests in the form:
    *
    *   it("test name") { }
    *
    */
   private fun KtCallExpression.tryIt(): String? {
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("it")
      return if (name == null) null else "It: $name"
   }

   /**
    * Finds tests in the form:
    *
    *   xit("test name") { }
    *
    */
   private fun PsiElement.tryXIt(): String? = ifType<KtCallExpression, String> {
      val name = it.extractStringArgForFunctionWithStringAndLambdaArgs("xit")
      if (name == null) null else "xIt: $name"
   }

   /**
    * Finds tests in the form:
    *
    *   xdescribe("test name") { }
    *
    */
   private fun KtCallExpression.tryXDescribe(): String? {
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("xdescribe")
      return if (name == null) null else "xDescribe: $name"
   }

   /**
    * Finds tests in the form:
    *
    *   it("test name").config { }
    *
    */
   private fun KtDotQualifiedExpression.tryItWithConfig(): String? {
      val test = extractLhsStringArgForDotExpressionWithRhsFinalLambda("it", "config")
      return if (test == null) null else "It: $test"
   }

   /**
    * Finds tests in the form:
    *
    *   xit("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryXItWithConfig(): String? {
      val test = extractLhsStringArgForDotExpressionWithRhsFinalLambda("xit", "config")
      return if (test == null) null else "It: $test"
   }

   fun test(element: PsiElement): Test? {
      if (!element.isContainedInSpec()) return null

      val name = when (element) {
         is KtCallExpression -> element.tryIt() ?: element.tryXIt() ?: element.tryDescribe() ?: element.tryXDescribe() ?: element.tryContext()
         is KtDotQualifiedExpression -> element.tryItWithConfig() ?: element.tryXItWithConfig()
         else -> null
      }

      return if (name == null) null else {
         val path = (locateParentContexts(element) + name).distinct().joinToString(" ")
         Test(name, path)
      }
   }

   override fun testPath(element: PsiElement): String? = test(element)?.path

   override fun testPath(element: LeafPsiElement): String? {
      if (!element.isContainedInSpec()) return null

      val call = element.ifCallExpressionNameIdent()
      if (call != null) return testPath(call)

      val dot = element.ifDotExpressionSeparator()
      if (dot != null) return testPath(dot)

      return null
   }
}
