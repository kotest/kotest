package io.kotest.plugin.intellij

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import javax.swing.Icon

fun createLineMarker(element: PsiElement, text: String, icon: Icon) = LineMarkerInfo(
   element,
   element.textRange,
   icon,
   { text },
   { _, _ -> },
   GutterIconRenderer.Alignment.LEFT
)
