package io.kotest.plugin.intellij.psi

import org.jetbrains.kotlin.psi.KtBlockStringTemplateEntry
import org.jetbrains.kotlin.psi.KtSimpleNameStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

fun KtStringTemplateExpression.asString(): StringArg {
   val text = buildString {
      for (entry in entries) {
         when (entry) {
            is KtSimpleNameStringTemplateEntry -> append("*")
            is KtBlockStringTemplateEntry -> append("*")
            else -> append(entry.text.replace("\\\"", "\""))
         }
      }
   }
   return StringArg(text, hasInterpolation())
}
