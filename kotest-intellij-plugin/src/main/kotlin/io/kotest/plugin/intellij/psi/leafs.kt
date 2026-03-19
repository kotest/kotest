package io.kotest.plugin.intellij.psi

import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * Return a [KtClassOrObject] if this leaf element is a pointer to such, otherwise, returns null.
 */
fun LeafPsiElement.asKtClassOrObjectOrNull(): KtClassOrObject? {
   return if (elementType is KtKeywordToken && (text == "class" || text == "object")) {
      when (val context = this.context) {
         is KtObjectDeclaration -> context
         is KtClass -> context
         else -> null
      }
   } else null
}
