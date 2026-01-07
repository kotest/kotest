package io.kotest.plugin.intellij.psi

import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

/**
 * For invocations of the form
 *
 * a("foo").b(...) { }
 *
 * returns the single string arg passed to the first function.
 */
fun KtDotQualifiedExpression.extractLhsStringArgForDotExpressionWithRhsFinalLambda(
   lhs: String,
   rhs: String
): StringArg? =
   extractLhsStringArgForDotExpressionWithRhsFinalLambda(listOf(lhs), listOf(rhs))

/**
 * For invocations of the form
 *
 * a("foo").b(...) { }
 *
 * returns the single string arg passed to the first function.
 */
fun KtDotQualifiedExpression.extractLhsStringArgForDotExpressionWithRhsFinalLambda(
   lhs: List<String>,
   rhs: List<String>
): StringArg? {
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
fun KtDotQualifiedExpression.extractStringForStringExtensionFunctonWithRhsFinalLambda(rhs: String): StringArg? {
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
