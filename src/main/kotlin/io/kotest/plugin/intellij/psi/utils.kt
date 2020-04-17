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
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList

/**
 * Extracts the string literal from things like:
 *
 *   "my test" {}
 */
fun KtCallExpression.extractStringFromStringInvokeWithLambda(): String? {
   if (children.size == 2) {
      val maybeStringTemplate = children[0]
      val maybeLambdaArgument = children[1]
      if (maybeStringTemplate is KtStringTemplateExpression && maybeLambdaArgument is KtLambdaArgument) {
         return maybeStringTemplate.asString()
      }
   }
   return null
}

fun KtCallExpression.functionName(): String? {
   val a = children[0]
   return if (a is KtNameReferenceExpression) a.text else null
}

fun KtCallExpression.hasFunctionName(names: List<String>): Boolean = names.contains(functionName())

/**
 * If this [KtCallExpression] has a single arg in it's value list,
 * and that single arg is of type [KtStringTemplateExpression], then it
 * returns the value of that arg, otherwise null
 */
fun KtCallExpression.getSingleStringArgOrNull(): String? {

   if (children.size < 2)
      return null

   val argList = children[1]
   if (argList is KtValueArgumentList && argList.children.size == 1) {
      val value = argList.children[0]
      if (value is KtValueArgument) {
         val maybeStringTemplateExpression = value.children[0]
         if (maybeStringTemplateExpression is KtStringTemplateExpression) {
            return maybeStringTemplateExpression.asString()
         }
      }
   }

   return null
}

fun PsiElement.isNameReference(names: List<String>): Boolean = this is KtNameReferenceExpression && names.contains(text)

/**
 * Returns the value of this string expression.
 */
fun KtStringTemplateExpression.asString(): String? {
   return when (val entry = children[0]) {
      is KtStringTemplateEntry -> entry.text
      else -> null
   }
}

/**
 * Returns true if the final argument to this call is a lambda arg
 */
fun KtCallExpression.hasFinalLambdaArg(): Boolean {
   if (children.size < 2)
      return false
   return lastChild is KtLambdaArgument
}

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
fun KtCallExpression.extractStringArgForFunctionWithStringAndLambdaArgs(vararg names: String): String? =
   extractStringArgForFunctionWithStringAndLambdaArgs(names.asList())

fun KtCallExpression.extractStringArgForFunctionWithStringAndLambdaArgs(names: List<String>): String? {
   if (children.size == 3
      && children[0].isNameReference(names)
      && children[1].isSingleStringTemplateArg()
      && children[2] is KtLambdaArgument) {
      return children[1] // KtValueArgumentList
         .children[0] // KtValueArgument
         .children[0] // KtStringTemplateExpression
         .children[0] // KtStringTemplateEntry
         .text
   }
   return null
}

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
 *
 */
fun LeafPsiElement.ifCallExpressionLambdaOpenBrace(): KtCallExpression? {
   if (text == "{") {
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
   }
   return null
}

/**
 * If this [LeafPsiElement] is the identifer used by the operation on a binary expression,
 * then returns that expression.
 */
fun LeafPsiElement.ifBinaryExpressionOperationIdent(): KtBinaryExpression? {
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
 * For invocations of the form a("foo").b(...) { } returns the single string arg passed to the first function.
 */
fun KtDotQualifiedExpression.extractLhsStringArgForDotExpressionWithRhsFinalLambda(lhs: String,
                                                                                   rhs: String): String? =
   extractLhsStringArgForDotExpressionWithRhsFinalLambda(listOf(lhs), listOf(rhs))

fun KtDotQualifiedExpression.extractLhsStringArgForDotExpressionWithRhsFinalLambda(lhs: List<String>,
                                                                                   rhs: List<String>): String? {
   if (children.size != 2)
      return null

   val a = children[0]
   val b = children[1]

   if (a is KtCallExpression && b is KtCallExpression) {
      if (a.hasFunctionName(lhs) && b.hasFunctionName(rhs)) {
         val testName = a.getSingleStringArgOrNull()
         if (testName != null && b.hasFinalLambdaArg())
            return testName
      }
   }

   return null
}

/**
 * For invocations of the form "foo".b(...) { } returns the string literal on the LHS
 */
fun KtDotQualifiedExpression.extractStringForStringExtensionFunctonWithRhsFinalLambda(rhs: String): String? {
   if (children.size != 2)
      return null

   val a = children[0]
   val b = children[1]

   if (a is KtStringTemplateExpression && b is KtCallExpression) {
      if (b.hasFunctionName(listOf(rhs)) && b.hasFinalLambdaArg()) {
         return a.asString()
      }
   }

   return null
}

fun PsiElement.isSingleStringTemplateArg(): Boolean =
   this is KtValueArgumentList
      && children.size == 1
      && children[0] is KtValueArgument
      && children[0].children.size == 1
      && children[0].children[0] is KtStringTemplateExpression


/**
 * Returns the string literal used by an infix function, when the function is of the
 * form <string literal> <operation> <lambda-expression>, eg, "test" should { } or "test" - {}
 */
fun KtBinaryExpression.extractStringLiteralFromLhsOfInfixFunction(names: List<String>): String? {
   if (children.size == 3) {
      val a = children[0]
      val b = children[1]
      val c = children[2]
      if (a is KtStringTemplateExpression
         && b is KtOperationReferenceExpression
         && c is KtLambdaExpression
         && names.contains(b.text)) {
         return a.asString()
      }
   }
   return null
}

fun buildSuggestedName(specName: String?, testName: String?): String? {
   return when {
      specName == null || specName.isBlank() -> null
      testName == null || testName.isBlank() -> specName.split('.').last()
      else -> {
         val simpleName = specName.split('.').last()
         "$simpleName: $testName"
      }
   }
}
