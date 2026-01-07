package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.enclosingKtClassOrObject
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtNamedFunction

object AnnotationSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.AnnotationSpec")

   override fun specStyleName(): String = "Annotation Spec"

   override fun generateTest(specName: String, name: String): String {
      return "@Test\nfun `$name`() { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtNamedFunction -> {
            // must be inside a class
            val specClass = element.enclosingKtClassOrObject() ?: return null
            // check for the presence of the annotation @Test
            element.modifierList?.annotationEntries?.find { it.text == "@Test" } ?: return null
            // element must be named
            val name = element.name ?: return null
            return Test(
               TestName(null, name, focus = false, bang = false, interpolated = false),
               null,
               specClass,
               TestType.Test,
               xdisabled = false,
               psi = element
            )
         }
         else -> null
      }
   }

   override fun possibleLeafElements(): Set<String> {
      return emptySet()
   }

   override fun test(element: LeafPsiElement): Test? {
      return if (element.parent is KtNamedFunction && element.text == "fun") {
         test(element.parent)
      } else null
   }
}
