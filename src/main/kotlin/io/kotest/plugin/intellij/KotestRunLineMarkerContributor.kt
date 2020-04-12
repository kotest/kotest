package io.kotest.plugin.intellij

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.Function
import io.kotest.plugin.intellij.psi.enclosingClassOrObjectForClassOrObjectToken
import io.kotest.plugin.intellij.psi.isSpecSubclass
import io.kotest.plugin.intellij.styles.SpecStyle
import io.kotest.plugin.intellij.styles.Test
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Given an element, returns an [RunLineMarkerContributor.Info] if the elements line should have a gutter icon added.
 */
class KotestRunLineMarkerContributor : RunLineMarkerContributor() {

   override fun getInfo(element: PsiElement): Info? {
      // the docs say to only run a line marker for a leaf
      return when (element) {
         is LeafPsiElement -> markerForSpec(element) ?: markerForTest(element)
         else -> null
      }
   }

   private fun markerForSpec(element: LeafPsiElement): Info? {
      val ktclass = element.enclosingClassOrObjectForClassOrObjectToken() ?: return null
      return SpecStyle.styles.asSequence()
         .filter { ktclass.isSpecSubclass(it) }
         .map { icon(ktclass) }
         .firstOrNull()
   }

   private fun markerForTest(element: LeafPsiElement): Info? {
      // println("Creating run marker for $element ${element.hashCode()}")
      return SpecStyle.styles.asSequence()
         .filter { it.isContainedInSpec(element) }
         .map { it.test(element) }
         .filterNotNull()
         .map { icon(it) }
         .firstOrNull()
   }

   private fun icon(ktclass: KtClassOrObject): Info {
      // println("Creating run icon for $test")
      return object : Info(
         AllIcons.RunConfigurations.TestState.Run_run,
         Function<PsiElement, String> { "Run ${ktclass.fqName!!.shortName()}" },
         *ExecutorAction.getActions(1)
      ) {
         override fun shouldReplace(other: Info): Boolean {
            println("shouldReplace $this $other")
            return false
         }
      }
   }

   private fun icon(test: Test): Info {
      // println("Creating run icon for $test")
      return object : Info(
         AllIcons.RunConfigurations.TestState.Run,
         Function<PsiElement, String> { "Run ${test.path}" },
         *ExecutorAction.getActions(1)
      ) {
         override fun shouldReplace(other: Info): Boolean {
            println("shouldReplace $this $other")
            return false
         }
      }
   }
}
