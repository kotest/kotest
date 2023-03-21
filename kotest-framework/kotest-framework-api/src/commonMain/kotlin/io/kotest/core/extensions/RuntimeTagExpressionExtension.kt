package io.kotest.core.extensions

import io.kotest.core.TagExpression

/**
 * Allows including/excluding tags at runtime using a tag expression.
 *
 * E.g., inside project config or a project listener, you can do:
 *
 * ```
 * RuntimeTagExpressionExtension.expression = "linux & mysql"
 * ```
 */
class RuntimeTagExpressionExtension(private val expression: String) : TagExtension {
   override fun tags(): TagExpression = TagExpression(expression)
}
