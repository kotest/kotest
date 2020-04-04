package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName

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
   fun tests(element: PsiElement): List<TestElement> {
      return element.children.flatMap { child ->
         val childTests = tests(child)
         val testPath = testPath(child)
         if (testPath != null) {
            childTests + TestElement(child, testPath, emptyList())
         } else childTests
      }
   }

   private fun PsiElement.tryContext() =
      this.matchFunction2WithStringAndLambda(listOf("context"))

   private fun PsiElement.tryTestWithoutConfig() =
      this.matchFunction2WithStringAndLambda(listOf("test"))

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

data class TestElement(val psi: PsiElement, val name: String, val tests: List<TestElement>)
