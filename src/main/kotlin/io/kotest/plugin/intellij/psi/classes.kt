package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * Returns true if this element is contained within a class that is a Spec class.
 */
fun PsiElement.isContainedInSpec(fqn: FqName): Boolean {
   val enclosingClass = getParentOfType<KtClassOrObject>(true) ?: return false
   return enclosingClass.isSpecSubclass(fqn)
}

/**
 * Returns the [KtClassOrObject] for a [LeafPsiElement] that is a [KtKeywordToken] with the
 * token value "class" or "object"
 */
fun LeafPsiElement.enclosingClassOrObjectForClassOrObjectToken(): KtClassOrObject? {
   if (elementType is KtKeywordToken && (text == "class" || text == "object")) {
      val maybeKtClassOrObject = parent
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
 */
fun KtClassOrObject.getSuperClass(): KtClassOrObject? {
   for (entry in superTypeListEntries) {
      if (entry is KtSuperTypeCallEntry) {

         val ref = entry.calleeExpression
            .constructorReferenceExpression
            ?.mainReference
            ?.resolve()

         if (ref is KtClassOrObject) return ref
         if (ref is KtPrimaryConstructor) return ref.getContainingClassOrObject()
      }
   }
   return null
}

/**
 * Returns the [KtClass] if this [LeafPsiElement] has type [KtKeywordToken] with the lexeme 'class'
 * and the [KtClass] definition is a subclass of the given spec style.
 */
fun PsiElement.enclosingClass(): KtClass? = getParentOfType(true)
