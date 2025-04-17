package io.kotest.core.extensions

import io.kotest.engine.tags.TagExpression

/**
 * Allows including/excluding tags at runtime using a tag expression.
 *
 * E.g., inside project config or a project listener, you can do:
 *
 * ```
 * RuntimeTagExpressionExtension("linux & mysql")
 * ```
 */
class RuntimeTagExpressionExtension(private val expression: String) : TagExtension {
   override fun tags(): TagExpression = TagExpression(expression)
}
