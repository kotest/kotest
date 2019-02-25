package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement

object DescribeSpecStyle : SpecStyle {

  override fun fqn(): String = "io.kotlintest.specs.DescribeSpec"

  override fun specStyleName(): String = "DescribeSpec"

  // todo this could be optimized to not check for the other parts of the tree until the name is needed
  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.locateParentTests(): List<String> {
    val test = tryContext() ?: tryDescribe()
    val result = if (test == null) emptyList() else listOf(test)
    // if parent is null then we have hit the end
    return if (parent == null) result else parent.locateParentTests() + result
  }

  private fun PsiElement.tryDescribe(): String? {
    val describe = matchFunction2WithStringAndLambda(listOf("describe"))
    return if (describe == null) null else "Describe: $describe"
  }

  private fun PsiElement.tryContext(): String? {
    val context = matchFunction2WithStringAndLambda(listOf("context"))
    return if (context == null) null else "Context: $context"
  }

  private fun PsiElement.tryIt(): String? {
    val scenario = matchFunction2WithStringAndLambda(listOf("it"))
    return if (scenario == null) null else "It: $scenario"
  }

  override fun testPath(element: PsiElement): String? {
    if (!element.isInSpecClass()) return null
    val test = element.tryIt() ?: element.tryContext() ?: element.tryDescribe()
    return if (test == null) null else element.locateParentTests().joinToString(" ") + " $test"
  }
}