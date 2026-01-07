package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.concurrency.annotations.RequiresReadLock
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.analysis.api.permissions.KaAllowAnalysisFromWriteAction
import org.jetbrains.kotlin.analysis.api.permissions.KaAllowAnalysisOnEdt
import org.jetbrains.kotlin.analysis.api.permissions.allowAnalysisFromWriteAction
import org.jetbrains.kotlin.analysis.api.permissions.allowAnalysisOnEdt
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
 * Returns true if this [KtClassOrObject] is a subclass of a spec, including classes
 * which are subclasses of intermediate classes.
 */
fun KtClassOrObject.isSpecEdt(): Boolean {
   return when (this) {
      is KtObjectDeclaration -> this.specStyleOnEdt() != null
      is KtClass -> this.specStyleOnEdt() != null
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

/**
 * Returns the spec style for this class if it is a subclass of a spec, or null otherwise.
 */
@OptIn(
   KaAllowAnalysisOnEdt::class,
   KaAllowAnalysisFromWriteAction::class,
)
@RequiresReadLock
fun KtClassOrObject.specStyleOnEdt(): SpecStyle? {
   allowAnalysisFromWriteAction {
      allowAnalysisOnEdt {
         val supers = getAllSuperClasses()
         return SpecStyle.styles.find { supers.contains(it.fqn()) }
      }
   }
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
fun PsiElement.isContainedInSpecEdt(): Boolean {
   val enclosingClass = getStrictParentOfType<KtClass>() ?: return false
   return enclosingClass.isSpecEdt()
}

/**
 * Returns the [KtClassOrObject] that contains this element if the element is located inside a
 * spec class, otherwise null.
 */
fun PsiElement.enclosingSpec(): KtClassOrObject? {
   val ktclassOrObject = this.getStrictParentOfType<KtClassOrObject>()
   return when {
      ktclassOrObject == null -> null
      ktclassOrObject.isSpec() -> ktclassOrObject
      else -> ktclassOrObject.enclosingSpec() // recurse with outer classes
   }
}
