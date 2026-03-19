package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList

/**
 * Extracts the string literal from things like:
 *
 *   "my test" {}
 */
fun KtCallExpression.extractStringFromStringInvokeWithLambda(): StringArg? {
   if (children.size == 2) {
      val maybeStringTemplate = children[0]
      val maybeLambdaArgument = children[1]
      if (maybeStringTemplate is KtStringTemplateExpression && maybeLambdaArgument is KtLambdaArgument) {
         return maybeStringTemplate.asString()
      }
   }
   return null
}

/**
 * Returns the name of the given function [KtCallExpression].
 */
fun KtCallExpression.functionName(): String? {
   val a = children[0]
   return if (a is KtNameReferenceExpression) a.text else null
}

fun KtCallExpression.hasFunctionName(names: List<String>): Boolean = names.contains(functionName())

/**
 * Returns true if this element is a name reference and the name is one of the given strings.
 */
fun PsiElement.isNameReference(names: List<String>): Boolean =
   this is KtNameReferenceExpression && names.contains(text)

/**
 * Returns true if the final argument to this call is a lambda arg
 */
fun KtCallExpression.hasFinalLambdaArg(): Boolean {
   if (children.size < 2)
      return false
   return lastChild is KtLambdaArgument
}

/**
 * Convenience call for [extractStringArgForFunctionWithStringAndLambdaArgs] with list of names.
 */
fun KtCallExpression.extractStringArgForFunctionWithStringAndLambdaArgs(vararg names: String): StringArg? =
   extractStringArgForFunctionWithStringAndLambdaArgs(names.asList())

/**
 * Matches code in the form:
 *
 *   function("some string") { }
 *
 * The structure in PSI for this is:
 *
 *  KtCallExpression (the function invocation)
 *    - KtNameReferenceExpression (the name of the function)
 *    - KtValueArgumentList
 *      - KtValueArgument (container wrapper for an argument, in this case the string name)
 *        - KtStringTemplateExpression (the expression for the string arg)
 *          - KtLiteralStringTemplateEntry (the raw string value, safe to call .text on)
 *    - KtLambdaArgument (the test closure)
 *      - KtLambdaArgument
 */
fun KtCallExpression.extractStringArgForFunctionWithStringAndLambdaArgs(names: List<String>): StringArg? {
   if (children.size == 3
      && children[0].isNameReference(names)
      && children[1] is KtValueArgumentList
      && children[2] is KtLambdaArgument
   ) {
      return (children[1] as KtValueArgumentList).getSingleStringArgOrNull()
   }
   return null
}

data class StringArg(val text: String, val interpolated: Boolean)

/**
 * If this [LeafPsiElement] is the open brace of a function lambda arg, then returns that function name.
 * Eg, test("my function") {}
 */
fun LeafPsiElement.ifCallExpressionLambdaOpenBrace(): KtCallExpression? {
   if (this.elementType.toString() != "LBRACE") return null
   val maybeFunctionLiteral = context
   if (maybeFunctionLiteral is KtFunctionLiteral) {
      val maybeLambdaExpression = maybeFunctionLiteral.context
      if (maybeLambdaExpression is KtLambdaExpression) {
         val maybeLambdaArg = maybeLambdaExpression.context
         if (maybeLambdaArg is KtLambdaArgument) {
            val maybeCallExpression = maybeLambdaArg.context
            if (maybeCallExpression is KtCallExpression)
               return maybeCallExpression
         }
      }
   }
   return null
}
