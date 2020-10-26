package io.kotest.engine.extensions

import io.kotest.core.Tags
import io.kotest.core.extensions.TagExtension

/**
 * Allows including/excluding tags at runtime using a tag expression.
 *
 * Eg, inside project config or a project listener, you can do:
 *
 * RuntimeTagExpressionExtension.expression = "linux & mysql"
 *
 */
object RuntimeTagExpressionExtension : TagExtension {
   var expression: String? = null
   override fun tags(): Tags = if (expression == null) Tags.Empty else Tags(expression)
}
