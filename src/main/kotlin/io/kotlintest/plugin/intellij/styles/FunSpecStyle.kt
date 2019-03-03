package io.kotlintest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName

object FunSpecStyle : SpecStyle {

  override fun fqn() = FqName("io.kotlintest.specs.FunSpec")

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

  private fun PsiElement.tryContext() = this.matchFunction2WithStringAndLambda(listOf("context"))
  private fun PsiElement.tryTestWithoutConfig() = this.matchFunction2WithStringAndLambda(listOf("test"))
  private fun PsiElement.tryTestWithConfig() = this.extractStringArgForFunctionBeforeDotExpr(listOf("test"),
      listOf("config"))

  override fun testPath(element: PsiElement): String? {
    if (!element.isContainedInSpec()) return null
    val test = element.tryTestWithoutConfig() ?: element.tryTestWithConfig() ?: element.tryContext()
    return if (test == null) null else {
      val paths = element.locateParentContexts() + test
      paths.distinct().joinToString(" -- ")
    }
  }
}