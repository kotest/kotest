package io.kotest.plugin.intellij

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

fun AnnotationHolder.createWarnAnnotation(element: PsiElement, msg: String) {
   newAnnotation(HighlightSeverity.WEAK_WARNING, msg).range(element).create()
}
