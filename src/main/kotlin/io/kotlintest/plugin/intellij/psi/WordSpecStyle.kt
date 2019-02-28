package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName

object WordSpecStyle : SpecStyle {

  override fun fqn() = FqName("io.kotlintest.specs.WordSpec")

  override fun specStyleName(): String = "WordSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.locateParentWhen(): String? {
    val wen = this.matchInfixFunctionWithStringAndLambaArg(listOf("when", "When"))
    return when {
      wen != null -> wen
      parent != null -> parent.locateParentWhen()
      else -> null
    }
  }

  private fun PsiElement.locateParentShould(): String? {
    val should = this.matchInfixFunctionWithStringAndLambaArg(listOf("should", "Should"))
    return when {
      should == null && parent == null -> null
      should == null -> parent.locateParentShould()
      else -> should
    }
  }

  private fun PsiElement.tryWhen(): String? =
      matchInfixFunctionWithStringAndLambaArg(listOf("when", "When"))

  private fun PsiElement.tryShould(): String? {
    val should = matchInfixFunctionWithStringAndLambaArg(listOf("should", "Should"))
    return if (should == null) null else {
      val w = parent.locateParentWhen()
      return if (w == null) should else "$w when $should"
    }
  }

  private fun PsiElement.trySubject(): String? {
    val subject = matchStringInvoke()
    return if (subject == null) null else {
      val should = locateParentShould()
      val w = locateParentWhen()
      when {
        should == null && w == null -> null
        w == null -> "$should should $subject"
        else -> "$w when $should should $subject"
      }
    }
  }

  private fun PsiElement.trySubjectWithConfig(): String? {
    val subject = extractLiteralForStringExtensionFunction(listOf("config"))
    return if (subject == null) null else {
      val should = locateParentShould()
      val w = locateParentWhen()
      when {
        should == null && w == null -> null
        w == null -> "$should should $subject"
        else -> "$w when $should should $subject"
      }
    }
  }

  override fun testPath(element: PsiElement): String? {
    if (!element.isContainedInSpec())
      return null
    return element.run {
      trySubject() ?: trySubjectWithConfig() ?: tryShould() ?: tryWhen()
    }
  }
}