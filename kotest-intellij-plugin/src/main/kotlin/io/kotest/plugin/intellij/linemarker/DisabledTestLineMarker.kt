package io.kotest.plugin.intellij.linemarker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.diff.util.DiffUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.existingEditor
import io.kotest.plugin.intellij.psi.enclosingKtClassOrObject
import io.kotest.plugin.intellij.psi.isTestFile
import io.kotest.plugin.intellij.psi.specStyle
import io.kotest.plugin.intellij.styles.SpecStyle
import io.kotest.plugin.intellij.testMode

/**
 * A [LineMarkerProvider] adds icons to gutters for elements.
 * This [DisabledTestLineMarker] adds the test-ignored icon to disabled kotest tests.
 */
class DisabledTestLineMarker : LineMarkerProvider {

   // icons list https://jetbrains.design/intellij/resources/icons_list/
   private val icon = AllIcons.RunConfigurations.TestIgnored

   private val possibleLeafElements = SpecStyle.styles.flatMap { it.possibleLeafElements() }

   override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
      // the docs say to only run a line marker for a leaf
      if (element !is LeafPsiElement) return null

      // only consider tests unless we are in fact testing
      if (!testMode && !ModuleUtil.hasTestSourceRoots(element.project)) return null
      if (!testMode && !element.containingFile.isTestFile()) return null

      // we don't show these line markers inside a diff
      val editor = element.existingEditor() ?: return null
      if (DiffUtil.isDiffEditor(editor)) return null

      // if the element is not one of the possible types our spec styles care about, then we can skip
      return if (possibleLeafElements.contains(element.elementType.toString())) markerForTest(element)
      else null
   }

   private fun markerForTest(element: LeafPsiElement): LineMarkerInfo<PsiElement>? {
      val ktclass = element.enclosingKtClassOrObject() ?: return null

      val style = ktclass.specStyle() ?: return null
      val test = style.test(element) ?: return null
      return if (test.enabled)
         null
      else
         MainEditorLineMarkerInfo(element, "Disabled - ${test.readableTestPath()}", icon)
   }
}
