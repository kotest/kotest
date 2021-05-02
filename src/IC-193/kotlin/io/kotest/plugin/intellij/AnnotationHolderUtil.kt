package io.kotest.plugin.intellij

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.psi.PsiElement

fun AnnotationHolder.createWarnAnnotation(element: PsiElement, msg: String) {
   createWeakWarningAnnotation(element, msg)
}
