package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement

object ExpectSpecStyle : SpecStyle {

  override fun specStyleName(): String = "ExpectSpec"

  // todo this could be optimized to not check for the other parts of the tree until the name is needed
  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.locateParentTests(): List<String> {
    val test = tryContext()
    val result = if (test == null) emptyList() else listOf(test)
    // if parent is null then we have hit the end
    return if (parent == null) result else parent.locateParentTests() + result
  }

  private fun PsiElement.tryExpect(): String? {
    val expect = matchFunction2WithStringAndLambda(listOf("expect"))
    return if (expect == null) null else "Expect: $expect"
  }

  private fun PsiElement.tryContext(): String? {
    val context = matchFunction2WithStringAndLambda(listOf("context"))
    return if (context == null) null else "Context: $context"
  }

  override fun testPath(element: PsiElement): String? {
    if (!element.isInSpecClass()) return null
    val test = element.tryExpect() ?: element.tryContext()
    return if (test == null) null else element.locateParentTests().joinToString(" ") + " $test"
  }
}