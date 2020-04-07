package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.extractLiteralForStringExtensionFunction
import io.kotest.plugin.intellij.psi.matchInfixFunctionWithStringAndLambaArg
import io.kotest.plugin.intellij.psi.matchStringInvoke
import org.jetbrains.kotlin.name.FqName

object FreeSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.FreeSpec")

   override fun specStyleName(): String = "Free Spec"

   override fun generateTest(specName: String, name: String): String {
      return "\"$name\" { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private fun PsiElement.locateParentTests(): List<String> {
      val test = tryBranch()
      val result = if (test == null) emptyList() else listOf(test)
      // if parent is null then we have hit the end
      return if (parent == null) result else parent.locateParentTests() + result
   }

   private fun PsiElement.tryBranch(): String? = matchInfixFunctionWithStringAndLambaArg(listOf("-"))
   private fun PsiElement.tryLeaf(): String? = matchStringInvoke()
   private fun PsiElement.tryLeafWithConfig(): String? = extractLiteralForStringExtensionFunction(listOf("config"))

   override fun test(element: PsiElement): Test? {
//    if (!element.isContainedInSpec()) return null
//    val test = element.tryLeaf() ?: element.tryLeafWithConfig() ?: element.tryBranch()
//    return if (test == null) null else {
//      val tests = element.locateParentTests() + test
//      tests.distinct().joinToString(" -- ")
//    }
      return null
   }
}
