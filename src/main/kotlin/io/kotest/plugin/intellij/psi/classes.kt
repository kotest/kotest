package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * Returns the [KtClassOrObject] if this [LeafPsiElement] has type [KtKeywordToken]
 * with the lexeme 'class' or 'object'
 */
fun LeafPsiElement.enclosingClassClassOrObjectToken(): KtClass? {
   if (elementType is KtKeywordToken && (text == "class" || text == "object")) {
      val maybeKtClassOrObject = context
      if (maybeKtClassOrObject is KtClass) {
         return maybeKtClassOrObject
      }
   }
   return null
}

/**
 * Returns the simple name of the parent class of this class.
 * Is efficient, does not require resolve.
 */
fun KtClass.getSuperClassSimpleName(): String? {
   for (entry in superTypeListEntries) {
      if (entry is KtSuperTypeCallEntry) {
         val name = entry.typeAsUserType?.referencedName
         if (name != null) return name
      }
   }
   return null
}

/**
 * Returns the [KtClass] from this light class, otherwise null.
 */
fun KtLightClass.toKtClass(): KtClass? = kotlinOrigin?.toKtClass()

/**
 * Returns true if this [KtClass] is a direct subclass of the given fully qualified name.
 */
private fun KtClass.isDirectSubclass(fqn: FqName): Boolean {
   return when (val simpleName = getSuperClassSimpleName()) {
      null -> false
      else -> fqn.shortName().asString() == simpleName
   }
}

/**
 * Returns true if this [KtClass] is a descendent of the given fully qualified name.
 * Recursively checks each parent.
 * Efficiently checks the first parent without requiring resolve, but requires resolve for
 * abstract parents.
 */
fun KtClass.isSubclass(fqn: FqName): Boolean {
   if (isDirectSubclass(fqn)) return true
   return getSuperClass()?.isSubclass(fqn) ?: false
}

/**
 * If this is an instance of [KtClass] returns this, otherwise returns null.
 */
fun KtClassOrObject.toKtClass(): KtClass? = if (this is KtClass) this else null

/**
 * Returns all [KtClass]s located in this [PsiFile]
 */
fun PsiFile.classes(): List<KtClass> {
   return this.getChildrenOfType<KtClass>().asList()
}

/**
 * Returns the first [KtClass] parent of this element.
 */
fun PsiElement.enclosingKtClass(): KtClass? = getParentOfType(false)

/**
 * Returns the superclass parent of this [KtClass].
 * If this class or object only implements interfaces then this function will
 * return null.
 *
 * This is slow because it will call resolve.
 */
fun KtClass.getSuperClass(): KtClass? {
   for (entry in superTypeListEntries) {
      if (entry is KtSuperTypeCallEntry) {

         val ref = entry.calleeExpression
            .constructorReferenceExpression
            ?.mainReference
            ?.resolve()

         if (ref is KtClass) return ref
         if (ref is KtPrimaryConstructor) return ref.getContainingClassOrObject().toKtClass()
      }
   }
   return null
}
