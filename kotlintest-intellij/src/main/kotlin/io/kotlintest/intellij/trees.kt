package io.kotlintest.intellij

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.refactoring.isAbstract
import org.jetbrains.kotlin.idea.refactoring.isInterfaceClass
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry

/**
 * Returns an FQN of a spec if the enclosing class or object for
 * the given element is a subclass of Spec.
 * If the enclosing class is not a Spec, then we return null.
 */
fun PsiElement.containingSpecName(): FqName? {
  return when (this) {
    is KtClassOrObject -> this.asSpecName()
    else -> this.parent?.containingSpecName()
  }
}

fun PsiElement.asSpecName(): FqName? {
  return when (this) {
    is KtClassOrObject -> if (this.isTestClass()) this.fqName else null
    else -> null
  }
}

/**
 * Returns true if the given class or object is an instance (subclass)
 * of a Spec. This function works by recursively testing each
 * superclass in turn until we either hit the top level, or we
 * find the AbstractSpec class.
 *
 * todo I would like to know how to test for interfaces as well, then we could search recursively for the Spec interface which would be nicer.
 */
fun KtClassOrObject.isSubclassOfSpec(): Boolean {
  val specFQN = "io.kotlintest.AbstractSpec"
  return this.superTypeListEntries.filterIsInstance<KtSuperTypeCallEntry>().any {
    val superClass = it.calleeExpression.constructorReferenceExpression?.mainReference?.resolve()
    when (superClass) {
      is KtClassOrObject -> if (superClass.getKotlinFqName().toString() == specFQN) true else superClass.isSubclassOfSpec()
      else -> false
    }
  }
}

fun KtClassOrObject.isTestClass(): Boolean = this.isSubclassOfSpec() && this.isTopLevel() && !this.isAbstract() && !this.isInterfaceClass()
