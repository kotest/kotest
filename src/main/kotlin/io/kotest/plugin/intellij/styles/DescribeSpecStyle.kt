package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList

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
    * A test of the form:
    *
    *   describe("test name") { }
    *
    * The structure in PSI for this is:
    *
    *  KtCallExpression (the function invocation)
    *    - KtNameReferenceExpression (the name of the test, in this case should be "describe")
    *    - KtValueArgumentList
    *      - KtValueArgument (container wrapper for an argument, in this case the string name)
    *        - KtStringTemplateExpression (the string value of the name of the test)
    *          - KtLiteralStringTemplateEntry (the raw string value, safe to call .text)
    *    - KtLambdaArgumnt (the test closure)
    *
    */
   private fun PsiElement.tryDescribe(): String? {
      return if (children.size == 3) {
         val a = children[0]
         val b = children[1]
         val c = children[2]
         if (a is KtNameReferenceExpression && a.text == "describe"
            && b is KtValueArgumentList
            && c is KtLambdaArgument) b.firstArgAsString() else null
      } else null
   }

   private fun PsiElement.tryContext(): String? {
      val test = matchFunction2WithStringAndLambda(listOf("context"))
      return if (test == null) null else "Context: $test"
   }

   /**
    * A test of the form:
    *
    *   it("test name") { }
    *
    * The structure in PSI for this is:
    *
    *  KtCallExpression (the function invocation)
    *    - KtNameReferenceExpression (the name of the test, in this case should be "it")
    *    - KtValueArgumentList
    *      - KtValueArgument (container wrapper for an argument, in this case the string name)
    *        - KtStringTemplateExpression (the string value of the name of the test)
    *          - KtLiteralStringTemplateEntry (the raw string value, safe to call .text)
    *    - KtLambdaArgumnt (the test closure)
    *
    */
   private fun PsiElement.tryIt(): String? {
      return if (children.size == 3) {
         val a = children[0]
         val b = children[1]
         val c = children[2]
         if (a is KtNameReferenceExpression && a.text == "it"
            && b is KtValueArgumentList
            && c is KtLambdaArgument) b.firstArgAsString() else null
      } else null
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
      val name = element.tryIt() ?: element.tryDescribe() ?: return null
      val path = (element.locateParentTests() + name).distinct().joinToString(" ")
      return Test(name, path)
   }

   override fun testPath(element: PsiElement): String? {
      if (!element.isContainedInSpec()) return null
      val test = element.run {
         tryIt() ?: tryItWithConfig() ?: tryContext() ?: tryDescribe() ?: return null
      }
      return (element.locateParentTests() + test).distinct().joinToString(" ")
   }
}
