package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.enclosingKtClassOrObject
import io.kotest.plugin.intellij.psi.extractLhsStringArgForDotExpressionWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringArgForFunctionWithStringAndLambdaArgs
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import io.kotest.plugin.intellij.psi.ifOpenQuoteOfFunctionName
import io.kotest.plugin.intellij.psi.isDataTestMethodCall
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object DescribeSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.DescribeSpec")

   override fun specStyleName(): String = "Describe Spec"

   override fun generateTest(specName: String, name: String): String {
      return "describe(\"$name\") { }"
   }

   override fun getDataTestMethodNames(): Set<String> =
      setOf(
         "withData",
         "withContexts",
         "withDescribes",
         "withIts"
      )

   private val fnNames = setOf("describe", "xdescribe", "context", "xcontext", "it", "xit")

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private fun locateParent(element: PsiElement): Test? {
      // if parent is null then we have hit the end
      val p = element.parent ?: return null
      fun parse(): Test? {
         return when (p) {
            is KtDotQualifiedExpression -> p.tryDescribeWithConfig()
               ?: p.tryXDescribeWithConfig()
               ?: p.tryContextWithConfig()
               ?: p.tryXContextWithConfig()
            is KtCallExpression -> p.tryDescribe()
               ?: p.tryXDescribe()
               ?: p.tryContext()
               ?: p.tryXContent()
            else -> null
         }
      }
      return parse() ?: locateParent(p)
   }

   /**
    * Finds tests in the form:
    *
    *   describe("test name") { }
    *
    */
   private fun KtCallExpression.tryDescribe(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("describe") ?: return null
      return buildTest(
         TestName(null, name.text, name.interpolated),
         name.text.startsWith("!"),
         this,
         TestType.Container,
         specClass
      )
   }

   /**
    * Finds tests in the form:
    *
    *   context("test name") { }
    *
    */
   private fun KtCallExpression.tryContext(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("context") ?: return null
      return buildTest(
         TestName(null, name.text, name.interpolated),
         name.text.startsWith("!"),
         this,
         TestType.Container,
         specClass
      )
   }

   /**
    * Finds tests in the form:
    *
    *   it("test name") { }
    *
    */
   private fun KtCallExpression.tryIt(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("it") ?: return null
      return buildTest(
         TestName(null, name.text, name.interpolated),
         name.text.startsWith("!"),
         this,
         TestType.Test,
         specClass
      )
   }

   /**
    * Finds tests in the form:
    *
    *   xit("test name") { }
    *
    */
   private fun KtCallExpression.tryXIt(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("xit") ?: return null
      return buildTest(TestName(null, name.text, name.interpolated), true, this, TestType.Test, specClass)
   }

   /**
    * Finds tests in the form:
    *
    *   xdescribe("test name") { }
    *
    */
   private fun KtCallExpression.tryXDescribe(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("xdescribe") ?: return null
      return buildTest(TestName(null, name.text, name.interpolated), true, this, TestType.Container, specClass)
   }

   /**
    * Finds tests in the form:
    *
    *   xcontext("test name") { }
    *
    */
   private fun KtCallExpression.tryXContent(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("xcontext") ?: return null
      return buildTest(TestName(null, name.text, name.interpolated), true, this, TestType.Container, specClass)
   }

   /**
    * Finds tests in the form:
    *
    *   it("test name").config { }
    *
    */
   private fun KtDotQualifiedExpression.tryItWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractLhsStringArgForDotExpressionWithRhsFinalLambda("it", "config") ?: return null
      return buildTest(
         TestName(null, name.text, name.interpolated),
         name.text.startsWith("!"),
         this,
         TestType.Test,
         specClass
      )
   }

   /**
    * Finds tests in the form:
    *
    *   xit("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryXItWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractLhsStringArgForDotExpressionWithRhsFinalLambda("xit", "config") ?: return null
      return buildTest(TestName(null, name.text, name.interpolated), true, this, TestType.Test, specClass)
   }

   /**
    * Finds tests in the form:
    *
    *   context("test name").config() { }
    *
    */
   private fun KtDotQualifiedExpression.tryContextWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractLhsStringArgForDotExpressionWithRhsFinalLambda("context", "config") ?: return null
      return buildTest(
         TestName(null, name.text, name.interpolated),
         name.text.startsWith("!"),
         this,
         TestType.Container,
         specClass
      )
   }

   /**
    * Finds tests in the form:
    *
    *   describe("test name").config() { }
    *
    */
   private fun KtDotQualifiedExpression.tryDescribeWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractLhsStringArgForDotExpressionWithRhsFinalLambda("describe", "config") ?: return null
      return buildTest(
         TestName(null, name.text, name.interpolated),
         name.text.startsWith("!"),
         this,
         TestType.Container,
         specClass
      )
   }

   /**
    * Finds tests in the form:
    *
    *   xdescribe("test name").config() { }
    *
    */
   private fun KtDotQualifiedExpression.tryXDescribeWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractLhsStringArgForDotExpressionWithRhsFinalLambda("xdescribe", "config") ?: return null
      return buildTest(TestName(null, name.text, name.interpolated), true, this, TestType.Container, specClass)
   }

   /**
    * Finds tests in the form:
    *
    *   xdescribe("test name").config() { }
    *
    */
   private fun KtDotQualifiedExpression.tryXContextWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val name = extractLhsStringArgForDotExpressionWithRhsFinalLambda("xcontext", "config") ?: return null
      return buildTest(TestName(null, name.text, name.interpolated), true, this, TestType.Container, specClass)
   }

   private fun buildTest(
      testName: TestName,
      xdisabled: Boolean,
      element: PsiElement,
      testType: TestType,
      specClass: KtClassOrObject,
   ): Test {
      return Test(testName, locateParent(element), specClass, testType, xdisabled, element)
   }

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.tryIt()
            ?: element.tryXIt()
            ?: element.tryDescribe()
            ?: element.tryXDescribe()
            ?: element.tryContext()
            ?: element.tryXContent()
            ?: element.tryDataTest()
         is KtDotQualifiedExpression ->
            element.tryDescribeWithConfig()
               ?: element.tryXDescribeWithConfig()
               ?: element.tryItWithConfig()
               ?: element.tryXItWithConfig()
               ?: element.tryContextWithConfig()
               ?: element.tryXContextWithConfig()
         else -> null
      }
   }

   override fun possibleLeafElements(): Set<String> {
      return setOf("OPEN_QUOTE", "DOT")
   }

   /**
    * For a DescribeSpec we consider the following scenarios:
    *
    * describe("test name") { }
    * xdescribe("test name") { }
    * context("test name") { }
    * xcontext("test name") { }
    * it("test name") { }
    * xit("test name") { }
    * describe("test name").config(...) {}
    * xdescribe("test name").config(...) {}
    * context("test name").config(...) {}
    * xcontext("test name").config(...) {}
    * it("test name").config(...) {}
    * xit("test name").config(...) {}
    * withData(...) { }
    * withContexts(...) { }
    * withDescribes(...) { }
    * withIts(...) { }
    */
   override fun test(element: LeafPsiElement): Test? {
      val call = element.ifOpenQuoteOfFunctionName(fnNames)
      if (call != null) return test(call)

      val dot = element.ifDotExpressionSeparator()
      if (dot != null) return test(dot)

      // try to find Data Test Method by finding lambda openings
      val dataMethodCall = element.isDataTestMethodCall(getDataTestMethodNames())
      if (dataMethodCall != null) {
         return test(dataMethodCall)
      }
      return null
   }
}
