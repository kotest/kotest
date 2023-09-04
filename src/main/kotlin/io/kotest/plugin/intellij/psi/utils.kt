package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
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
 * If this [LeafPsiElement] is the dot between two calls in a dot expression, returns that dot expression.
 */
fun LeafPsiElement.ifDotExpressionSeparator(): KtDotQualifiedExpression? {
   if (text == ".") {
      val maybeDotQualifiedExpression = parent
      if (maybeDotQualifiedExpression is KtDotQualifiedExpression) {
         return maybeDotQualifiedExpression
      }
   }
   return null
}

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

/**
 * If this [LeafPsiElement] is the identifer used by the operation on a binary expression,
 * then returns that expression.
 */
fun LeafPsiElement.ifMinusOperator(): KtBinaryExpression? {
   if (this.elementType.toString() != "MINUS") return null
   val maybeOperationReferenceExpression = parent
   if (maybeOperationReferenceExpression is KtOperationReferenceExpression) {
      val maybeBinaryExpression = maybeOperationReferenceExpression.parent
      if (maybeBinaryExpression is KtBinaryExpression) {
         return maybeBinaryExpression
      }
   }
   return null
}

/**
 * If this [LeafPsiElement] is the opening quote of a string literal used on the left hand side
 * of a call expression, returns that call expression.
 *
 * This should be used to detect expressions of the form "some test" {}
 */
fun LeafPsiElement.ifCallExpressionLhsStringOpenQuote(): KtCallExpression? {
   if (this.elementType.toString() != "OPEN_QUOTE") return null
   val maybeStringTemplateExpression = parent
   if (maybeStringTemplateExpression is KtStringTemplateExpression) {
      val maybeCallExpression = maybeStringTemplateExpression.parent
      if (maybeCallExpression is KtCallExpression) {
         return maybeCallExpression
      }
   }
   return null
}

/**
 * If this [LeafPsiElement] is the opening quote of a string literal used inside
 * a function with one of the given names, then returns that function.
 *
 * This should be used to detect expressions of the form fn("some test") {}
 */
fun LeafPsiElement.ifOpenQuoteOfFunctionName(fnNames: Set<String>): KtCallExpression? {
   if (this.elementType.toString() != "OPEN_QUOTE") return null
   val maybeStringTemplateExpression = parent
   if (maybeStringTemplateExpression is KtStringTemplateExpression) {
      val maybeValueArgument = maybeStringTemplateExpression.parent
      if (maybeValueArgument is KtValueArgument) {
         val maybeValueArgumentList = maybeValueArgument.parent
         if (maybeValueArgumentList is KtValueArgumentList) {
            val maybeCallExpression = maybeValueArgumentList.parent
            if (maybeCallExpression is KtCallExpression) {
               val name = maybeCallExpression.functionName()
               if (name != null && fnNames.contains(name))
                  return maybeCallExpression
            }
         }
      }
   }
   return null
}

/**
 * If this [KtCallExpression] has a single arg in it's value list, returns that string, otherwise null.
 */
fun KtCallExpression.getSingleStringArgOrNull(): StringArg? {
   return when {
      children.size < 2 -> null
      children[1] is KtValueArgumentList -> (children[1] as KtValueArgumentList).getSingleStringArgOrNull()
      else -> null
   }
}

/**
 * If this [KtValueArgumentList] has a single argument of type [KtStringTemplateExpression],
 * returns the value of the expression, otherwise null.
 */
fun KtValueArgumentList.getSingleStringArgOrNull(): StringArg? {
   if (children.size == 1) {
      val arg = children[0]
      if (arg is KtValueArgument) {
         if (arg.children.size == 1) {
            val template = arg.children[0]
            if (template is KtStringTemplateExpression) {
               return template.asString()
            }
         }
      }
   }
   return null
}

/**
 * Returns true if this argument has a single argument of type string.
 */
fun KtValueArgumentList.isSingleStringTemplateArg(): Boolean =
   children.size == 1
      && children[0] is KtValueArgument
      && children[0].children.size == 1
      && children[0].children[0] is KtStringTemplateExpression
