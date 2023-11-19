package io.kotest.plugin.intellij.psi

import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isAbstract

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

/**
 * Return a [KtClassOrObject] if this leaf element is a pointer to a kclass or object, and that
 * class is not abstract, and it is a subclass of a spec, otherwise null.
 */
fun LeafPsiElement.asSpecEntryPoint(): KtClassOrObject? {
   val classOrObject = asKtClassOrObjectOrNull() ?: return null
   return when (classOrObject) {
      is KtObjectDeclaration -> if (classOrObject.isSpec()) classOrObject else null
      is KtClass -> if (classOrObject.isSpec() && !classOrObject.isAbstract()) classOrObject else null
      else -> null
   }
}
