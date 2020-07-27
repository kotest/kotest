package io.kotest.plugin.intellij.psi

import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

/**
 * Returns the string literal used by an infix function, when the function is of the
 * form <string literal> <operation> <lambda-expression>, eg, "test" should { } or "test" - {}
 */
fun KtBinaryExpression.extractStringLiteralFromLhsOfInfixFunction(names: List<String>): StringArg? {
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
