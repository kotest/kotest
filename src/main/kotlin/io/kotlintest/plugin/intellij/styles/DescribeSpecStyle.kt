package io.kotlintest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName

object DescribeSpecStyle : SpecStyle {

  override fun fqn() = FqName("io.kotlintest.specs.DescribeSpec")

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

  private fun PsiElement.tryDescribe(): String? {
    val test = matchFunction2WithStringAndLambda(listOf("describe"))
    return if (test == null) null else "Describe: $test"
  }

  private fun PsiElement.tryContext(): String? {
    val test = matchFunction2WithStringAndLambda(listOf("context"))
    return if (test == null) null else "Context: $test"
  }

  private fun PsiElement.tryIt(): String? {
    val test = matchFunction2WithStringAndLambda(listOf("it"))
    return if (test == null) null else "It: $test"
  }

  private fun PsiElement.tryItWithConfig(): String? {
    val test = extractStringArgForFunctionBeforeDotExpr(listOf("it"), listOf("config"))
    return if (test == null) null else "It: $test"
  }

  override fun testPath(element: PsiElement): String? {
    if (!element.isContainedInSpec()) return null
    val test = element.run {
      tryIt() ?: tryItWithConfig() ?: tryContext() ?: tryDescribe() ?: return null
    }
    return (element.locateParentTests() + test).distinct().joinToString(" ")
  }
}