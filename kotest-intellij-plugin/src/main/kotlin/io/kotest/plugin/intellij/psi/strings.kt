package io.kotest.plugin.intellij.psi

import org.jetbrains.kotlin.psi.KtStringTemplateExpression

fun KtStringTemplateExpression.asString(): StringArg {
   return StringArg(entries.joinToString("") { it.text.replace("\\\"", "\"") }, hasInterpolation())
}
