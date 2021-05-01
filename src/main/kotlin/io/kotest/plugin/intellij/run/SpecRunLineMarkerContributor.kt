package io.kotest.plugin.intellij.run

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.getSpecEntryPoint
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Adds a 'run' icon marker if an element is the entry point to a spec.
 *
 * The entry point is the 'class' or 'object' keyword that is part of the spec's
 * definition in code. In psi terms, this is a leaf element whose element type is KTK, and context
 * element is [KtClassOrObject].
 */
class SpecRunLineMarkerContributor : RunLineMarkerContributor() {

   // icons list https://jetbrains.design/intellij/resources/icons_list/
   private val icon = AllIcons.RunConfigurations.TestState.Run

   override fun getInfo(element: PsiElement): Info? {
      when (element) {
         // the docs say to only run a line marker for a leaf
         is LeafPsiElement -> {
            val spec = element.getSpecEntryPoint()
            if (spec != null) {
               return Info(
                  icon,
                  com.intellij.util.Function<PsiElement, String> { "Run ${spec.fqName?.shortName()}" },
                  *ExecutorAction.getActions(1)
               )
            }
         }
      }
      return null
   }
}
