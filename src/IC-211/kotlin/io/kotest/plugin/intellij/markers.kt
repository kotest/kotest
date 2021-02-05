package io.kotest.plugin.intellij

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.util.Functions
import java.util.function.Supplier
import javax.swing.Icon

fun createLineMarker(element: PsiElement, text: String, icon: Icon) = LineMarkerInfo<PsiElement>(
   element,
   element.textRange,
   icon,
   Functions.constant(text),
   GutterIconNavigationHandler<PsiElement> { _, _ -> },
   GutterIconRenderer.Alignment.LEFT,
   Supplier<String> { text }
)
