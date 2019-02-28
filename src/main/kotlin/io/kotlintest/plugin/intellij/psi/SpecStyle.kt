package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName

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

  fun PsiElement.isContainedInSpec(): Boolean = this.isContainedInSpec(fqn())

  fun testPath(element: PsiElement): String?

  fun specStyleName(): String

  fun isTestElement(element: PsiElement): Boolean

  fun fqn(): FqName
}