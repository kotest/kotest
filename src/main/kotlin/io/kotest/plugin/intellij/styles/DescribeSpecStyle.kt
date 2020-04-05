package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.ifType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression

object DescribeSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.DescribeSpec")

   override fun specStyleName(): String = "DescribeSpec"

   override fun generateTest(specName: String, name: String): String {
      return "describe(\"$name\") { }"
   }

   // todo this could be optimized to not check for the other parts of the tree until the name is needed
   override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

   private fun PsiElement.locateParentTests(): List<String> {
      val test = tryContext() ?: tryDescribe()
      val result = if (test == null) emptyList() else listOf(test)
      // if parent is null then we have hit the end
      return if (parent == null) result else parent.locateParentTests() + result
   }

   /**
    * Finds tests in the form:
    *
    *   describe("test name") { }
    *
    */
   private fun PsiElement.tryDescribe(): String? = ifType<KtCallExpression, String> {
      val name = it.extractStringArgForFunction2WithStringAndLambda("describe")
      if (name == null) null else "Describe: $name"
   }

   /**
    * Finds tests in the form:
    *
    *   context("test name") { }
    *
    */
   private fun PsiElement.tryContext(): String? = ifType<KtCallExpression, String> {
      val name = it.extractStringArgForFunction2WithStringAndLambda("context")
      if (name == null) null else "Context: $name"
   }

   /**
    * Finds tests in the form:
    *
    *   it("test name") { }
    *
    */
   private fun PsiElement.tryIt(): String? = ifType<KtCallExpression, String> {
      val name = it.extractStringArgForFunction2WithStringAndLambda("it")
      if (name == null) null else "It: $name"
   }

   /**
    * Finds tests in the form:
    *
    *   xit("test name") { }
    *
    */
   private fun PsiElement.tryXit(): String? = ifType<KtCallExpression, String> {
      val name = it.extractStringArgForFunction2WithStringAndLambda("xit")
      if (name == null) null else "xIt: $name"
   }

   /**
    * Finds tests in the form:
    *
    *   xdescribe("test name") { }
    *
    */
   private fun PsiElement.tryXdescribe(): String? = ifType<KtCallExpression, String> {
      val name = it.extractStringArgForFunction2WithStringAndLambda("xdescribe")
      return if (name == null) null else "xDescribe: $name"
   }

   private fun PsiElement.tryItWithConfig(): String? {
      val test = extractStringArgForFunctionBeforeDotExpr(listOf("it"), listOf("config"))
      return if (test == null) null else "It: $test"
   }

   /**
    * Returns all child tests located in the given [PsiElement].
    */
   override fun tests(element: PsiElement): List<TestElement> {
      return element.children.flatMap { child ->
         val childTests = tests(child)
         val test = test(child)
         if (test != null) {
            listOf(TestElement(child, test, childTests))
         } else childTests
      }
   }

   fun test(element: PsiElement): Test? {
      if (!element.isContainedInSpec()) return null
      val name = element.tryIt() ?: element.tryDescribe()
      if (name != null) {
         val path = (element.locateParentTests() + name).distinct().joinToString(" ")
         return Test(name, path)
      }
      val xname = element.tryXit() ?: element.tryXdescribe()
      if (xname != null) {
         val path = (element.locateParentTests() + xname).distinct().joinToString(" ")
         return Test(xname, path, false)
      }
      return null
   }

   override fun testPath(element: PsiElement): String? {
      if (!element.isContainedInSpec()) return null
      val test = element.run {
         tryIt() ?: tryItWithConfig() ?: tryContext() ?: tryDescribe() ?: return null
      }
      return (element.locateParentTests() + test).distinct().joinToString(" ")
   }

}
