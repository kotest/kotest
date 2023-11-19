package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration

fun PsiElement.asKtClassOrObjectOrNull(): KtClassOrObject? = when (this) {
   is KtObjectDeclaration -> this
   is KtClass -> this
   else -> null
}
