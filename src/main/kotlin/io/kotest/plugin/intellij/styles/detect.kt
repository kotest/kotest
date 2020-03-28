package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

/**
 * Returns any [KtClassOrObject]s located in this [PsiElement]
 */
fun PsiElement.classes(): List<KtClassOrObject> {
   return this.getChildrenOfType<KtClassOrObject>().asList()
}

fun PsiElement.specs(): List<KtClassOrObject> {
   return this.classes().filter { it.isAnySpecSubclass() }
}
