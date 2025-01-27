package io.kotest.plugin.intellij.breadcrumbs

import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import org.jetbrains.kotlin.idea.KotlinLanguage

class KotestBreadcrumbProvider : BreadcrumbsProvider {

   override fun isShownByDefault(): Boolean {
      return true
   }

   override fun getLanguages(): Array<out Language?>? {
      println("LANGUAGES")
      return arrayOf(KotlinLanguage.INSTANCE)
   }

   override fun acceptElement(p0: PsiElement): Boolean {
      println("acceptElement: $p0")
      return true
   }

   override fun getElementInfo(p0: PsiElement): String {
      println("getElementInfo: $p0")
      return "foo"
   }
}
