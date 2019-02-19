package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement

object ShouldSpecStyle : SpecStyle {

  override fun specStyleName(): String = "ShouldSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.locateParentTests(): List<String> {
    val test = tryContainer()
    val result = if (test == null) emptyList() else listOf(test)
    // if parent is null then we have hit the end
    return if (parent == null) result else parent.locateParentTests() + result
  }

  private fun PsiElement.tryShouldWithConfig(): String? {
    val should = extractStringArgForFunctionBeforeDotExpr(listOf("should"), listOf("config"))
    return if (should == null) null else "should $should"
  }

  private fun PsiElement.tryShould(): String? {
    val should = matchFunction2WithStringAndLambda(listOf("should"))
    return if (should == null) null else "should $should"
  }

  private fun PsiElement.tryContainer(): String? {
    return matchStringInvoke()
  }

  override fun testPath(element: PsiElement): String? {
    if (!element.isInSpecClass()) return null
    val test = element.tryShould() ?: element.tryShouldWithConfig() ?: element.tryContainer()
    return if (test == null) null else element.locateParentTests().joinToString(" ") + " $test"
  }
}