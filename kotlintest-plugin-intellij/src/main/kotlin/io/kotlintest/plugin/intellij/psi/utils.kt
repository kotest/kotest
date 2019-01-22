package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstructorCalleeExpression
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * Finds the enclosing class for the receiver [PsiElement] and then
 * locates the super types. Returns true if a super type corresponds to the given spec style.
 */
fun PsiElement.isInSpecStyle(name: String): Boolean {
  val ktclass = getParentOfType<KtClass>(true)
  if (ktclass != null) {
    if (ktclass.children.isNotEmpty() && ktclass.children[0] is KtSuperTypeList) {
      val ktsuper = ktclass.children[0]
      if (ktsuper.children.isNotEmpty() && ktsuper.children[0] is KtSuperTypeCallEntry) {
        val ktsupercall = ktsuper.children[0]
        if (ktsupercall.children.isNotEmpty() && ktsupercall.children[0] is KtConstructorCalleeExpression) {
          val ktconstructor = ktsupercall.children[0]
          if (ktconstructor.text == name) return true
        }
      }
    }
  }
  return false
}

fun PsiElement.isSingleStringArgList(): Boolean = when (this) {
  is KtValueArgumentList -> children.size == 1 && children[0] is KtValueArgument
  else -> false
}

fun PsiElement.enclosingClassName(): String? {
  val ktclass = getParentOfType<KtClass>(true)
  return ktclass?.fqName?.asString()
}