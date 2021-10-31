package io.kotest.core.extensions

import io.kotest.core.Tags

/**
 * Allows including/excluding tags at runtime using a tag expression.
 *
 * Eg, inside project config or a project listener, you can do:
 *
 * RuntimeTagExpressionExtension.expression = "linux & mysql"
 *
 */
class RuntimeTagExpressionExtension(private val expression: String) : TagExtension {
   override fun tags(): Tags = Tags(expression)
}
