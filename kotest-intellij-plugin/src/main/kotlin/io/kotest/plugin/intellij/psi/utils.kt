package io.kotest.plugin.intellij.psi

import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList

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
 * If this [LeafPsiElement] is the opening quote of a string literal on the LHS
 * of an infix function call, and the function has one of the given names, then
 * returns that function
 *
 * This should be used to detect expressions of the form fn("some test") {}
 */
fun LeafPsiElement.ifOpenQuoteOfLhsArgOfIndexFunction(fnNames: Set<String>): KtBinaryExpression? {
   if (this.elementType.toString() != "OPEN_QUOTE") return null
   val maybeStringTemplateExpression = parent
   if (maybeStringTemplateExpression is KtStringTemplateExpression) {
      val maybeBinaryExpression = maybeStringTemplateExpression.parent
      if (maybeBinaryExpression is KtBinaryExpression) {
         if (maybeBinaryExpression.right is KtLambdaExpression && fnNames.contains(maybeBinaryExpression.operationReference.text))
            return maybeBinaryExpression
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

fun LeafPsiElement.isDataTestMethodCall(dataTestMethodNames:Set<String>): KtCallExpression? {
   val lambdaCall = ifCallExpressionLambdaOpenBrace()
   return lambdaCall.takeIf {lambdaCall?.functionName() in dataTestMethodNames}
}
