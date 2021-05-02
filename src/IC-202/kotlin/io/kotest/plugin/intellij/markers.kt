package io.kotest.plugin.intellij

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.MarkupEditorFilter
import com.intellij.openapi.editor.markup.MarkupEditorFilterFactory
import com.intellij.psi.PsiElement
import javax.swing.Icon

/**
 * A Line marker that does not appear in diffs
 */
class MainEditorLineMarkerInfo(element: PsiElement, text: String, icon: Icon) : LineMarkerInfo<PsiElement>(
   element, element.textRange, icon, { text }, { _, _ -> }, GutterIconRenderer.Alignment.LEFT
) {
   override fun getEditorFilter(): MarkupEditorFilter = MarkupEditorFilterFactory.createIsNotDiffFilter()
}
