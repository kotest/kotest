package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList

/**
 * Matches blocks of the form:
 *
 * functionName("some string").config(..)
 *
 * @return the string argument of the invoked function
 *
 * @param lefts one or more acceptable names for the left hand side reference
 */
@Deprecated("")
fun PsiElement.extractStringArgForFunctionWithConfig(lefts: List<String>): String? =
   extractStringArgForFunctionBeforeDotExpr(lefts, listOf("config"))

/**
 * Matches blocks of the form:
 *
 * functionName("some string").<ident>
 *
 * Eg, can be used to match: should("this is a test").config { }
 *
 * @return the string argument of the invoked function
 *
 * @param lefts one or more acceptable names for the left hand side reference
 * @param rights one or more acceptable names for the right hand side reference
 */
@Deprecated("")
fun PsiElement.extractStringArgForFunctionBeforeDotExpr(lefts: List<String>, rights: List<String>): String? {
   if (parent is KtLiteralStringTemplateEntry) {
      val maybeTemplateExpr = parent.parent
      if (maybeTemplateExpr is KtStringTemplateExpression) {
         val maybeValueArg = maybeTemplateExpr.parent
         if (maybeValueArg is KtValueArgument) {
            val maybeValueArgList = maybeValueArg.parent
            if (maybeValueArgList is KtValueArgumentList) {
               val maybeCallExpr = maybeValueArgList.parent
               if (maybeCallExpr is KtCallExpression) {
                  val maybeDotExpr = maybeCallExpr.parent
                  if (maybeDotExpr is KtDotQualifiedExpression) {
                     if (maybeDotExpr.children.size == 2
                        && maybeDotExpr.children[0].isCallExprWithName(lefts)
                        && maybeDotExpr.children[1].isCallExprWithName(rights)) {
                        return parent.text
                     }
                  }
               }
            }
         }
      }
   }
   return null
}

fun PsiElement.extractLiteralForStringExtensionFunction(funcnames: List<String>): String? {
   if (parent is KtLiteralStringTemplateEntry) {
      val maybeTemplateExpr = parent.parent
      if (maybeTemplateExpr is KtStringTemplateExpression) {
         val maybeDotExpr = maybeTemplateExpr.parent
         if (maybeDotExpr is KtDotQualifiedExpression) {
            if (maybeDotExpr.children.size == 2
               && maybeDotExpr.children[1].isCallExprWithName(funcnames)) {
               return parent.text
            }
         }
      }
   }
   return null
}

fun PsiElement.isCallExprWithName(names: List<String>): Boolean =
   this is KtCallExpression
      && children.isNotEmpty()
      && children[0] is KtReferenceExpression
      && names.contains(children[0].text)

fun KtCallExpression.hasName(name: String): Boolean {
   val a = children[0]
   return a is KtNameReferenceExpression && a.text == name
}

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

fun PsiElement.isOperation(names: List<String>): Boolean = this is KtOperationReferenceExpression && names.contains(text)

/**
 * Matches blocks of the form:
 *
 * functionName("some string") { }
 *
 * Eg, can be used to match: given("this is a test") { }
 *
 * @return the string argument of the invoked function
 *
 * @param names one or more function names to search for
 */
fun PsiElement.matchFunction2WithStringAndLambda(names: List<String>): String? {
   return when (val p = parent) {
      is KtStringTemplateEntry -> p.extractStringForFunction2WithStringAndLambda(names)
      is KtCallExpression -> p.extractStringArgForFunction2WithStringAndLambda(names)
      else -> null
   }
}

@Deprecated("")
fun KtStringTemplateEntry.extractStringForFunction2WithStringAndLambda(names: List<String>): String? {
   if (parent is KtStringTemplateExpression) {
      val maybeValueArg = parent.parent
      if (maybeValueArg is KtValueArgument) {
         val maybeValueArgList = maybeValueArg.parent
         if (maybeValueArgList is KtValueArgumentList) {
            val maybeCallExpr = maybeValueArgList.parent
            if (maybeCallExpr is KtCallExpression) {
               if (maybeCallExpr.children.size == 3
                  && maybeCallExpr.children[0].isNameReference(names)
                  && maybeCallExpr.children[2] is KtLambdaArgument) {
                  return text
               }
            }
         }
      }
   }
   return null
}

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
 *    - KtLambdaArgumnt (the test closure)
 */
fun KtCallExpression.extractStringArgForFunction2WithStringAndLambda(vararg names: String): String? =
   extractStringArgForFunction2WithStringAndLambda(names.asList())

fun KtCallExpression.extractStringArgForFunction2WithStringAndLambda(names: List<String>): String? {
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
   val maybeDotQualifiedExpression = parent
   if (maybeDotQualifiedExpression is KtDotQualifiedExpression) {
      return maybeDotQualifiedExpression
   }
   return null
}

/**
 * If this [LeafPsiElement] is the name of a function call, then returns that function.
 */
fun LeafPsiElement.ifCallExpressionName(): KtCallExpression? {
   val maybeNameReferenceExpression = parent
   if (maybeNameReferenceExpression is KtNameReferenceExpression) {
      val maybeCallExpression = maybeNameReferenceExpression.parent
      if (maybeCallExpression is KtCallExpression) {
         return maybeCallExpression
      }
   }
   return null
}

/**
 * For invocations of the form a("foo").b() { } returns the single string arg passed to the first function.
 */
fun KtDotQualifiedExpression.extractLhsStringArgForDotExpressionWithRhsFinalLambda(lhs: String,
                                                                                   rhs: String): String? {
   if (children.size != 2)
      return null

   val a = children[0]
   val b = children[1]

   if (a is KtCallExpression && b is KtCallExpression) {
      if (a.hasName(lhs) && b.hasName(rhs)) {
         val testName = a.getSingleStringArgOrNull()
         if (testName != null && b.hasFinalLambdaArg())
            return testName
      }
   }

   return null;
}

fun PsiElement.isSingleStringTemplateArg(): Boolean =
   this is KtValueArgumentList
      && children.size == 1
      && children[0] is KtValueArgument
      && children[0].children.size == 1
      && children[0].children[0] is KtStringTemplateExpression


/**
 * Matches blocks of the form:
 *
 * "string" infixFunctionName { }
 *
 * Eg, can be used to match: "this test" should { }
 *
 * @return the LHS operand
 *
 * @param names one or more function names to search for
 */
fun PsiElement.matchInfixFunctionWithStringAndLambaArg(names: List<String>): String? =
   when (val p = parent) {
      is KtStringTemplateEntry -> p.extractLhsForInfixFunction(names)
      is KtBinaryExpression -> p.extractLhsForInfixFunction(names)
      else -> null
   }

fun KtStringTemplateEntry.extractLhsForInfixFunction(names: List<String>): String? {
   if (parent is KtStringTemplateExpression) {
      val maybeBinaryExpr = parent.parent
      if (maybeBinaryExpr is KtBinaryExpression) {
         if (maybeBinaryExpr.children.size == 3
            && maybeBinaryExpr.children[1].isOperation(names)
            && maybeBinaryExpr.children[2] is KtLambdaExpression) {
            return text
         }
      }
   }
   return null
}

fun KtBinaryExpression.extractLhsForInfixFunction(names: List<String>): String? {
   if (children.size == 3
      && children[0] is KtStringTemplateExpression
      && children[1].isOperation(names)
      && children[2] is KtLambdaExpression) {
      val template = children[0]
      if (template.children.isNotEmpty() && template.children[0] is KtStringTemplateEntry) {
         return template.children[0].text
      }
   }
   return null
}

/**
 * Matches blocks of the form:
 *
 * "string" { }
 *
 * @return the LHS operand
 */
fun PsiElement.matchStringInvoke(): String? =
   when (val p = parent) {
      is KtStringTemplateEntry -> p.extractLhsForStringInvoke()
      is KtCallExpression -> p.extractLhsForStringInvoke()
      else -> null
   }

fun KtStringTemplateEntry.extractLhsForStringInvoke(): String? {
   if (parent is KtStringTemplateExpression) {
      val maybeCallExpr = parent.parent
      if (maybeCallExpr is KtCallExpression) {
         if (maybeCallExpr.children.size == 2
            && maybeCallExpr.children[1] is KtLambdaArgument) {
            return text
         }
      }
   }
   return null
}

fun KtCallExpression.extractLhsForStringInvoke(): String? {
   if (children.size == 2
      && children[0] is KtStringTemplateExpression
      && children[1] is KtLambdaArgument) {
      return children[0] // KtStringTemplateExpression
         .children[0] // KtStringTemplateEntry
         .text
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
