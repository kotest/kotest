package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement

object WordSpecStyle : SpecStyle {

  override fun specStyleName(): String = "WordSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.locateParentTestName(): String? {
    val param = this.matchInfixFunctionWithStringAndLambaArg(listOf("should"))
    return if (param == null && parent == null) null else param ?: parent.locateParentTestName()
  }

  private fun PsiElement.tryWhen(): String? =
      matchInfixFunctionWithStringAndLambaArg(listOf("when"))

  private fun PsiElement.tryShould(): String? =
      matchInfixFunctionWithStringAndLambaArg(listOf("should"))

  private fun PsiElement.trySubject(): String? {
    val subject = matchStringInvoke()
    return if (subject == null) null else {
      val should = locateParentTestName()
      return "$should should $subject"
    }
  }

  override fun testPath(element: PsiElement): String? {
    if (!element.isInSpecClass())
      return null
    return element.trySubject() ?: element.tryShould() ?: element.tryWhen()
  }
}