package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
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
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Returns all [KtClass] children of this [PsiFile] that are instances of a spec class.
 */
fun PsiFile.specs(): List<KtClass> {
   return this.classes().filter { it.isDirectSubclassOfSpec() }
}

/**
 * Returns true if this [KtClass] is a descendent of any Spec.
 * This method will recursively check parents.
 */
fun KtClass.isSubclassOfSpec(): Boolean = this.specStyle() != null

fun KtLightClass.isSubclassOfSpec(): Boolean = this.specStyle() != null

/**
 * Returns true if this [KtClass] is a subclass of any Spec.
 * This method will not recursively check parents, it will only check the immediate parent.
 * It relies on the simple name of spec classes, eg FunSpec rather than io.kotest....FunSpec
 */
fun KtClass.isDirectSubclassOfSpec(): Boolean = this.specStyle() != null

/**
 * Efficiently locates the [SpecStyle] for this class, or null if this class is not a spec.
 */
fun KtClass.specStyle(): SpecStyle? {
   val supername = getSuperClassSimpleName()
   val style = SpecStyle.styles.find { it.fqn().shortName().asString() == supername }
   return style ?: getSuperClass()?.specStyle()
}

/**
 * Efficiently locates the [SpecStyle] for this class, or null if this class is not a spec.
 */
fun KtLightClass.specStyle(): SpecStyle? {
   val supername = superClass?.name ?: return null
   val style = SpecStyle.styles.find { it.fqn().shortName().asString() == supername }
   if (style != null) return style
   return when (val s = superClass) {
      is KtClass -> s.specStyle()
      is KtLightClass -> s.specStyle()
      else -> null
   }
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

/**
 * If this call expression is an include(factory) or include(factory()) then will
 * return an [Include] describing that.
 *
 * Otherwise returns null.
 */
fun KtCallExpression.include(): Include? {
   if (children.isNotEmpty() &&
      children[0] is KtNameReferenceExpression &&
      children[0].text == "include") {
      val args = valueArgumentList
      if (args != null) {
         val maybeKtValueArgument = args.arguments.firstOrNull()
         if (maybeKtValueArgument is KtValueArgument) {
            when (val param = maybeKtValueArgument.children.firstOrNull()) {
               is KtCallExpression -> {
                  val name = param.children[0].text
                  return Include(name, IncludeType.Function, param)
               }
               is KtNameReferenceExpression -> {
                  val name = param.text
                  return Include(name, IncludeType.Value, param)
               }
            }
         }
      }
   }
   return null
}

/**
 * Returns any include operations defined in this class.
 */
fun KtClassOrObject.includes(): List<Include> {

   val body = this.getChildrenOfType<KtClassBody>().firstOrNull()
   if (body != null) return body.includes()

   val superlist = this.getChildrenOfType<KtSuperTypeList>().firstOrNull()
   if (superlist != null) return superlist.includes()

   return emptyList()
}

fun KtClassBody.includes(): List<Include> {
   val init = getChildrenOfType<KtClassInitializer>().firstOrNull()
   if (init != null) {
      val block = init.getChildrenOfType<KtBlockExpression>().firstOrNull()
      if (block != null) {
         return block.includes()
      }
   }
   return emptyList()
}

fun KtSuperTypeList.includes(): List<Include> {
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
                     return block.includes()
                  }
               }
            }

         }
      }
   }
   return emptyList()
}

fun KtBlockExpression.includes(): List<Include> {
   val calls = getChildrenOfType<KtCallExpression>()
   return calls.mapNotNull { it.include() }
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

/**
 * Returns the Spec that contains this element, or null if this element is not located inside a spec class.
 */
fun PsiElement.enclosingSpec(): KtClass? {
   val ktclass = this.getStrictParentOfType<KtClass>()
   return when {
      ktclass == null -> null
      ktclass.isSubclassOfSpec() -> ktclass
      else -> ktclass.enclosingSpec()
   }
}


enum class IncludeType { Value, Function }

data class Include(val name: String, val type: IncludeType, val psi: PsiElement)

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
   val enclosingClass = getStrictParentOfType<KtClass>() ?: return false
   return enclosingClass.isSubclass(fqn)
}

/**
 * Returns true if this [PsiElement] is located inside a class that subclasses any spec.
 */
fun PsiElement.isContainedInSpec(): Boolean {
   val enclosingClass = getStrictParentOfType<KtClass>() ?: return false
   return enclosingClass.isSubclassOfSpec()
}

