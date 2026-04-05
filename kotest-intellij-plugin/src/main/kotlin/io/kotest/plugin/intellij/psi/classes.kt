package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.idea.stubindex.KotlinClassShortNameIndex
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtUserType
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
 * Returns the [SpecStyle] for this class by syntactically matching supertype names against
 * the known Kotest spec styles. Traverses the inheritance hierarchy recursively (via the
 * project's stub index) so that classes which extend an intermediate abstract base spec are
 * also recognised.  No type resolution (Analysis API) is required.
 *
 * A visited set guards against infinite loops in circular (i.e. semantically invalid)
 * class hierarchies.
 */
internal fun KtClassOrObject.kotestStyleSyntactic(): SpecStyle? = kotestStyleSyntactic(mutableSetOf())

private fun KtClassOrObject.kotestStyleSyntactic(visited: MutableSet<String>): SpecStyle? {
   val key = fqName?.asString() ?: return null
   if (!visited.add(key)) return null

   val directSuperNames = superTypeListEntries
      .mapNotNull { (it.typeReference?.typeElement as? KtUserType)?.referencedName }

   // 1. A direct supertype is a known Kotest spec style — return immediately.
   SpecStyle.styles.firstOrNull { it.fqn().shortName().asString() in directSuperNames }
      ?.let { return it }

   // 2. Recurse: resolve each direct supertype by short name via the stub index and check
   //    the hierarchy transitively.
   val scope = GlobalSearchScope.allScope(project)
   return directSuperNames.asSequence()
      .flatMap { superName ->
         KotlinClassShortNameIndex[superName, project, scope].toList()
      }.firstNotNullOfOrNull { it.kotestStyleSyntactic(visited) }
}

/**
 * Returns the first [KtClass] parent of this element.
 */
fun PsiElement.enclosingKtClass(): KtClass? = getStrictParentOfType()

fun PsiElement.enclosingKtClassOrObject(): KtClassOrObject? =
   PsiTreeUtil.getParentOfType(this, KtClassOrObject::class.java)

/**
 * Returns true if this [KtClassOrObject] points to a runnable spec object.
 */
fun KtClassOrObject.isRunnableSpec(): Boolean = when (this) {
   is KtObjectDeclaration -> isSpec()
   is KtClass -> isSpec() && !isAbstract()
   else -> false
}

fun KtClassOrObject.takeIfRunnableSpec(): KtClassOrObject? = if (isRunnableSpec()) this else null
