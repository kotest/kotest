package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.asJava.classes.KtUltraLightClass
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Returns all [KtClassOrObject] children of this [PsiFile] that are instances of a spec class.
 */
fun PsiFile.specs(): List<KtClassOrObject> {
   return this.classes().filter { it.isSpec() }
}

/**
 * Returns true if this [KtClassOrObject] is a subclass of a spec, including classes
 * which are subclasses of intermediate classes.
 */
fun KtClassOrObject.isSpec(): Boolean {
   return when (this) {
      is KtObjectDeclaration -> this.specStyle() != null
      is KtClass -> this.specStyle() != null
      else -> false
   }
}

/**
 * Returns true if this element is a kotlin class, and it is a subclass of a spec.
 *
 * See [isSpec]
 */
fun PsiElement.isSpec(): Boolean = when (this) {
   is KtUltraLightClass -> kotlinOrigin.isSpec()
   is KtLightClass -> kotlinOrigin?.isSpec() ?: false
   is KtClassOrObject -> isSpec()
   else -> false
}

/**
 * Returns the spec style for this class if it is a subclass of a spec, or null otherwise.
 */
fun KtClassOrObject.specStyle(): SpecStyle? {
   val supers = getAllSuperClasses()
   return SpecStyle.styles.find { supers.contains(it.fqn()) }
}

fun KtCallExpression.isDslInvocation(): Boolean {
   return children.size == 2
      && children[0] is KtNameReferenceExpression
      && children[1] is KtLambdaArgument
}

/**
 * Returns true if this [PsiElement] is contained within a class that is a subclass
 * of the given spec FQN.
 */
fun PsiElement.isContainedInSpecificSpec(fqn: FqName): Boolean {
   val enclosingClass = getStrictParentOfType<KtClass>() ?: return false
   return enclosingClass.isSubclass(fqn)
}

/**
 * Returns true if this [PsiElement] is located inside a class that subclasses any spec.
 */
fun PsiElement.isContainedInSpec(): Boolean {
   val enclosingClass = getStrictParentOfType<KtClass>() ?: return false
   return enclosingClass.isSpec()
}

/**
 * Returns the Spec that contains this element, or null if this element is not located inside a spec class.
 */
fun PsiElement.enclosingSpec(): KtClass? {
   val ktclass = this.getStrictParentOfType<KtClass>()
   return when {
      ktclass == null -> null
      ktclass.isSpec() -> ktclass
      else -> ktclass.enclosingSpec()
   }
}
