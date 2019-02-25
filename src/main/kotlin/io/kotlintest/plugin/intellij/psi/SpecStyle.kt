package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement

interface SpecStyle {

  companion object {
    val specs = listOf(
        BehaviorSpecStyle,
        DescribeSpecStyle,
        ExpectSpecStyle,
        FeatureSpecStyle,
        FreeSpecStyle,
        FunSpecStyle,
        ShouldSpecStyle,
        StringSpecStyle,
        WordSpecStyle
    )
  }

  fun PsiElement.isInSpecClass(): Boolean = this.isInSpecStyle(specStyleName())

  fun testPath(element: PsiElement): String?

  fun specStyleName(): String

  fun isTestElement(element: PsiElement): Boolean

  fun fqn(): String
}