package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

enum class IncludeType { Value, Function }

data class Include(val name: String, val type: IncludeType, val psi: PsiElement)

/**
 * Returns any test factory 'include' definitions defined in this class.
 */
fun KtClassOrObject.includes(): List<Include> {

   val body = this.getChildrenOfType<KtClassBody>().firstOrNull()
   if (body != null) return body.includes()

   val superlist = this.getChildrenOfType<KtSuperTypeList>().firstOrNull()
   if (superlist != null) return superlist.includes()

   return emptyList()
}

/**
 * Returns any test factory 'include' functions defined in this class body.
 */
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

/**
 * Returns any test factory 'include' function calls defined in this block.
 */
fun KtBlockExpression.includes(): List<Include> {
   val calls = getChildrenOfType<KtCallExpression>()
   return calls.mapNotNull { it.include() }
}

/**
 * If this call expression is an include(factory) or include(factory()) then will
 * return an [Include] describing that, otherwise, returns null.
 */
fun KtCallExpression.include(): Include? {
   if (children.isNotEmpty() &&
      children[0] is KtNameReferenceExpression &&
      children[0].text == "include"
   ) {
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
