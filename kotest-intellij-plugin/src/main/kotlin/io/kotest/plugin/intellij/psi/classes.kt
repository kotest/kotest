package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.isAbstract

/**
 * Returns true if this [KtClassOrObject] is a descendent of the given class.
 */
fun KtClassOrObject.isSubclass(fqn: FqName): Boolean = getAllSuperClasses().contains(fqn)

/**
 * Returns all [KtClass]s located in this [PsiFile]
 */
fun PsiFile.classes(): List<KtClass> {
   return this.getChildrenOfType<KtClass>().asList()
}

/**
 * Returns the first [KtClass] parent of this element.
 */
fun PsiElement.enclosingKtClass(): KtClass? = getStrictParentOfType()

fun PsiElement.enclosingKtClassOrObject(): KtClassOrObject? =
   PsiTreeUtil.getParentOfType(this, KtClassOrObject::class.java)

/**
 * Returns true if this [KtClassOrObject] points to a runnable spec object.
 *
 * A runnable spec is a class that subclasses [Spec] and is not abstract.
 */
fun KtClassOrObject.isRunnableSpec(): Boolean = when (this) {
   is KtObjectDeclaration -> isSpec()
   is KtClass -> isSpec() && !isAbstract()
   else -> false
}

fun KtClassOrObject.takeIfRunnableSpec(): KtClassOrObject? = if (isRunnableSpec()) this else null

/**
 * Returns all superclasses and interfaces extended or implemented by the class, recursively, so all
 * parents are included, up to the root of the class hierarchy.
 */
fun KtClassOrObject.getAllSuperClasses(): List<FqName> {
   return superTypeListEntries.mapNotNull { it.typeReference }
      .flatMap { ref ->
         analyze(this) {
            val kaType = ref.type
            val superTypes = (kaType.allSupertypes(false) + kaType).toList()
            superTypes.mapNotNull {
               val classId = it.symbol?.classId?.takeIf { id -> id != StandardClassIds.Any }
               classId?.asSingleFqName()
            }
         }
      }
}

/**
 * Returns the direct list of classes and interfaces extended or implemented by the class.
 */
fun KtClassOrObject.immediateSuperClasses(): List<FqName> {
   return superTypeListEntries.mapNotNull { it.typeReference }
      .mapNotNull { ref ->
         analyze(this) {
            val kaType = ref.type
            val classId = kaType.symbol?.classId
            classId?.asSingleFqName()
         }
      }
}
