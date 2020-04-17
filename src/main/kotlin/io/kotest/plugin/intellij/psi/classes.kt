package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * Returns the [KtClassOrObject] if this [LeafPsiElement] has type [KtKeywordToken]
 * with the lexeme 'class' or 'object'
 */
fun LeafPsiElement.enclosingClassOrObjectForClassOrObjectToken(): KtClassOrObject? {
   if (elementType is KtKeywordToken && (text == "class" || text == "object")) {
      val maybeKtClassOrObject = context
      if (maybeKtClassOrObject is KtClassOrObject) {
         return maybeKtClassOrObject
      }
   }
   return null
}

/**
 * Returns the simple name of the parent class of this class.
 */
fun KtClassOrObject.getSuperClassSimpleName(): String? {
   for (entry in superTypeListEntries) {
      if (entry is KtSuperTypeCallEntry) {
         val name = entry.typeAsUserType?.referencedName
         if (name != null) return name
      }
   }
   return null
}

/**
 * Returns the superclass parent of this [KtClassOrObject].
 * If this class or object only implements interfaces then this function will
 * return null.
 *
 * This is slow because it will call resolve.
 */
//@Deprecated("Try to avoid this method will calls resolve")
//fun KtClassOrObject.getSuperClass(): KtClassOrObject? {
//   for (entry in superTypeListEntries) {
//      if (entry is KtSuperTypeCallEntry) {
//
//         val ref = entry.calleeExpression
//            .constructorReferenceExpression
//            ?.mainReference
//            ?.resolve()
//
//         if (ref is KtClassOrObject) return ref
//         if (ref is KtPrimaryConstructor) return ref.getContainingClassOrObject()
//      }
//   }
//   return null
//}

/**
 * Returns the first [KtClass] parent of this element.
 */
fun PsiElement.enclosingClass(): KtClass? {
   val parent = getParentOfType<KtClass>(false)
   return parent
}
