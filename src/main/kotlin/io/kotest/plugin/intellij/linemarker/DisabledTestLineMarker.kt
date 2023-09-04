package io.kotest.plugin.intellij.linemarker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.diff.util.DiffUtil
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.MainEditorLineMarkerInfo
import io.kotest.plugin.intellij.existingEditor
import io.kotest.plugin.intellij.psi.enclosingKtClassOrObject
import io.kotest.plugin.intellij.psi.isTestFile
import io.kotest.plugin.intellij.psi.specStyle
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtDeclarationModifierList
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPackageDirective

/**
 * Adds an icon to the gutter for tests which are disabled.
 */
class DisabledTestLineMarker : LineMarkerProvider {

   // icons list https://jetbrains.design/intellij/resources/icons_list/
   private val icon = AllIcons.RunConfigurations.TestIgnored

   override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
      // the docs say to only run a line marker for a leaf
      if (element !is LeafPsiElement) return null
      // we don't show these line markers inside a diff
      val editor = element.existingEditor() ?: return null
      if (DiffUtil.isDiffEditor(editor)) return null

      return when (element.elementType.toString()) {
         "WHITE_SPACE", "CLOSING_QUOTE", "LPAR", "RPAR", "REGULAR_STRING_PART", "EQ", "OPEN_QUOTE", "EOL_COMMENT", "IDENTIFIER" -> null
         else -> markerForTest(element)
      }
   }

   private fun markerForTest(element: LeafPsiElement): LineMarkerInfo<PsiElement>? {
      println(element.toString() + " = " + element.name + "; " + element.elementType.toString())
      val ktclass = element.enclosingKtClassOrObject() ?: return null
//      println(ktclass.containingFile.isTestFile())
//      if (!ktclass.containingFile.isTestFile()) return null

      val style = ktclass.specStyle() ?: return null
      val test = style.test(element) ?: return null
      return if (test.enabled)
         null
      else
         MainEditorLineMarkerInfo(element, "Disabled - ${test.readableTestPath()}", icon)
   }
}
