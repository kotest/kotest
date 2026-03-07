package io.kotest.plugin.intellij.linemarker

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.asKtClassOrObjectOrNull
import io.kotest.plugin.intellij.psi.isRunnableSpec
import io.kotest.plugin.intellij.psi.isTestFile
import io.kotest.plugin.intellij.testMode

/**
 * A [RunLineMarkerContributor] adds gutter icons to elements if they are actionable.
 *
 * This [SpecRunLineMarkerContributor] adds the test run icon to kotest spec classes.
 *
 * The entry point is the 'class' or 'object' keyword that is part of the spec's
 * definition in code. In psi terms, this is a leaf element whose element type is KTK, and context
 * element is [org.jetbrains.kotlin.psi.KtClassOrObject].
 */
class SpecRunLineMarkerContributor : RunLineMarkerContributor() {
   override fun getInfo(element: PsiElement): Info? {
      when (element) {
         // the docs say to only run a line marker for a leaf
         is LeafPsiElement -> {
            // only consider tests
            if (!testMode && !ModuleUtil.hasTestSourceRoots(element.project)) return null
            if (!testMode && !element.containingFile.isTestFile()) return null
            val spec = element.asKtClassOrObjectOrNull()
            if (spec != null && spec.isRunnableSpec()) {
               val fqn = spec.fqName?.asString()
               val icon = if (fqn != null) {
                  val status = LineMarkerUtils.determineSpecStatus(element, fqn)
                  LineMarkerUtils.determineIconFromStatus(status, specIcon = true)
               } else {
                  LineMarkerUtils.runRunIcon
               }
               return Info(
                  icon,
                  ExecutorAction.getActions(1),
               )
               // note that the run name is used for the tooltip not the drop down
               // the drop down gets names from the created run configurations
               { "Run ${spec.fqName?.shortName()}" }
            }
         }
      }
      return null
   }
}
