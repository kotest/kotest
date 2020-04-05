package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.map
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList

object FunSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.FunSpec")

   override fun specStyleName(): String = "FunSpec"

   override fun generateTest(specName: String, name: String): String {
      return "test(\"$name\") { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

   private fun PsiElement.locateParentContexts(): List<String> {
      val test = tryContext()
      val result = if (test == null) emptyList() else listOf(test)
      // if parent is null then we have hit the end
      return if (parent == null) result else parent.locateParentContexts() + result
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

   private fun PsiElement.tryContext() =
      this.matchFunction2WithStringAndLambda(listOf("context"))

   /**
    * A test is of the form:
    *
    *   test("test name") { }
    *
    * The structure in PSI for this is:
    *
    *  KtCallExpression (the function invocation)
    *    - KtNameReferenceExpression (the name of the test, in this case should be "test")
    *    - KtValueArgumentList
    *      - KtValueArgument (container wrapper for an argument, in this case the string name)
    *        - KtStringTemplateExpression (the string value of the name of the test)
    *          - KtLiteralStringTemplateEntry (the raw string value, safe to call .text)
    *    - KtLambdaArgumnt (the test closure)
    *
    */
   private fun PsiElement.tryTestWithoutConfig() = map<KtCallExpression, String?> {
      if (children.size == 3) {
         val a = children[0]
         val b = children[1]
         val c = children[2]
         if (a is KtNameReferenceExpression && a.text == "test"
            && b is KtValueArgumentList
            && c is KtLambdaArgument) b.firstArgAsString() else null
      } else null
   }

   private fun PsiElement.tryTestWithConfig() =
      this.extractStringArgForFunctionBeforeDotExpr(listOf("test"), listOf("config"))

   /**
    * Returns the test path for a given [PsiElement], or if this element is not a test, then returns null.
    */
   override fun testPath(element: PsiElement): String? {
      if (!element.isContainedInSpec()) return null
      val test = element.tryTestWithoutConfig() ?: element.tryTestWithConfig() ?: element.tryContext()
      return if (test == null) null else {
         val paths = element.locateParentContexts() + test
         paths.distinct().joinToString(" -- ")
      }
   }
}

data class Test(val name: String, val path: String, val enabled: Boolean) {
   constructor(name: String, path: String) : this(name, path, !name.startsWith("!"))
}

data class TestElement(val psi: PsiElement,
                       val test: Test,
                       val tests: List<TestElement>)
