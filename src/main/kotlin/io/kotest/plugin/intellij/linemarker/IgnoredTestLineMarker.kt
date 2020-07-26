package io.kotest.plugin.intellij.linemarker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.enclosingKtClass
import io.kotest.plugin.intellij.psi.isTestFile
import io.kotest.plugin.intellij.psi.specStyle
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtDeclarationModifierList
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPackageDirective

class IgnoredTestLineMarker : LineMarkerProvider {

   override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
      if (!element.containingFile.isTestFile()) return null

      // the docs say to only run a line marker for a leaf
      return when (element) {
         // ignoring white space elements will save a lot of lookups
         is PsiWhiteSpace -> null
         is LeafPsiElement -> {
            when (element.context) {
               // rule out some common entries that can't possibly be test markers for performance
               is KtAnnotationEntry, is KtDeclarationModifierList, is KtImportDirective, is KtImportList, is KtPackageDirective -> null
               else -> markerForTest(element)
            }
         }
         else -> null
      }
   }

   private fun markerForTest(element: LeafPsiElement): LineMarkerInfo<PsiElement>? {
      val ktclass = element.enclosingKtClass() ?: return null
      val style = ktclass.specStyle() ?: return null
      val test = style.test(element) ?: return null
      return if (test.enabled) null else LineMarkerInfo<PsiElement>(
         element,
         element.textRange,
         AllIcons.Nodes.TestIgnored,
         { "Test is disabled" },
         { _, _ -> },
         GutterIconRenderer.Alignment.LEFT
      )
   }
}
