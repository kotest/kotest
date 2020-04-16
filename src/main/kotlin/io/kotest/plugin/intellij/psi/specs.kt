package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * Returns any [KtClassOrObject]s located in this [PsiElement]
 */
fun PsiElement.classes(): List<KtClassOrObject> {
   return this.getChildrenOfType<KtClassOrObject>().asList()
}

/**
 * Returns any [KtClassOrObject] children of this [PsiFile] that are specs.
 */
fun PsiFile.specs(): List<KtClassOrObject> {
   return this.classes().filter { it.isSubclassOfSpec() }
}

/**
 * Returns true if this [PsiElement] is inside a spec class.
 */
fun PsiElement.isContainedInSpec(): Boolean {
   val enclosingClass = getParentOfType<KtClassOrObject>(true) ?: return false
   return enclosingClass.isSubclassOfSpec()
}

/**
 * Efficiently returns true if this [KtClassOrObject] is a subclass of any Spec.
 * This function will recursively check all superclasses.
 */
fun KtClassOrObject.isSubclassOfSpec(): Boolean {
   // gets the [KtClassOrObject] instance for the superclass, can be null
   val superClass = getSuperClass()
   if (superClass != null) {
      val fqn = superClass.getKotlinFqName() ?: return false
      return SpecStyle.styles.any { it.fqn() == fqn } || superClass.isSubclassOfSpec()
   }
   // sometimes we don't have the full superclass type, but we can get the simple name
   val superClassSimpleName = getSuperClassSimpleName()
   if (superClassSimpleName != null) {
      return SpecStyle.styles.any { it.fqn().shortName().asString() == superClassSimpleName }
   }
   return false
}

/**
 * Returns true if this [KtClassOrObject] is a subclass of a specific Spec.
 * This function will recursively check all superclasses.
 */
fun KtClassOrObject.isSpecSubclass(style: SpecStyle) = isSpecSubclass(style.fqn())

fun KtClassOrObject.isSpecSubclass(fqn: FqName): Boolean {
   val superClass = getSuperClass() ?: return getSuperClassSimpleName() == fqn.shortName().asString()
   return if (superClass.getKotlinFqName() == fqn) true else superClass.isSpecSubclass(fqn)
}

/**
 * Efficiently locates the spec style this class is from, or null if it's not a spec.
 */
fun KtClassOrObject.specStyle(): SpecStyle? {
   val superClass = getSuperClass()
   if (superClass != null) {
      val fqn = superClass.getKotlinFqName() ?: return null
      return SpecStyle.styles.find { it.fqn() == fqn } ?: superClass.specStyle()
   }
   // sometimes we don't have the full superclass type, but we can get the simple name
   val superClassSimpleName = getSuperClassSimpleName()
   if (superClassSimpleName != null) {
      return SpecStyle.styles.find { it.fqn().shortName().asString() == superClassSimpleName }
   }
   return null
}

fun KtCallExpression.isDslInvocation(): Boolean {
   return children.size == 2
      && children[0] is KtNameReferenceExpression
      && children[1] is KtLambdaArgument
}

/**
 * Returns any test lifecycle callbacks defined in this class.
 */
fun KtClassOrObject.callbacks(): List<Callback> {

   val body = this.getChildrenOfType<KtClassBody>().firstOrNull()
   if (body != null) return body.callbacks()

   val superlist = this.getChildrenOfType<KtSuperTypeList>().firstOrNull()
   if (superlist != null) return superlist.callbacks()

   return emptyList()
}

fun KtClassBody.callbacks(): List<Callback> {
   val init = getChildrenOfType<KtClassInitializer>().firstOrNull()
   if (init != null) {
      val block = init.getChildrenOfType<KtBlockExpression>().firstOrNull()
      if (block != null) {
         return block.callbacks()
      }
   }
   return emptyList()
}

fun KtSuperTypeList.callbacks(): List<Callback> {
   val entry = getChildrenOfType<KtSuperTypeCallEntry>().firstOrNull()
   if (entry != null) {
      val argList = entry.getChildrenOfType<KtValueArgumentList>().firstOrNull()
      if (argList != null) {
         val valueArg = argList.getChildrenOfType<KtValueArgument>().firstOrNull()
         if (valueArg != null) {
            val lambda = valueArg.getChildrenOfType<KtLambdaExpression>().firstOrNull()
            if (lambda != null) {
               val fliteral = lambda.getChildrenOfType<KtFunctionLiteral>().firstOrNull()
               if (fliteral != null) {
                  val block = fliteral.getChildrenOfType<KtBlockExpression>().firstOrNull()
                  if (block != null) {
                     return block.callbacks()
                  }
               }
            }

         }
      }
   }
   return emptyList()
}

fun KtBlockExpression.callbacks(): List<Callback> {
   val calls = getChildrenOfType<KtCallExpression>()
   return calls
      .filter { it.isDslInvocation() }
      .mapNotNull { call ->
         val fname = call.functionName()
         CallbackType.values().find { it.text == fname }?.let { Callback(it, call) }
      }
}

data class Callback(val type: CallbackType, val psi: PsiElement)

enum class CallbackType {

   BeforeTest {
      override val text = "beforeTest"
   },
   AfterTest {
      override val text = "afterTest"
   },
   BeforeSpec {
      override val text = "beforeSpec"
   },
   AfterSpec {
      override val text = "afterSpec"
   };

   abstract val text: String
}

/**
 * Returns true if this [PsiElement] is contained within a class that is a subclass
 * of the given spec FQN
 */
fun PsiElement.isContainedInSpec(fqn: FqName): Boolean {
   val enclosingClass = getParentOfType<KtClassOrObject>(true) ?: return false
   return enclosingClass.isSpecSubclass(fqn)
}
