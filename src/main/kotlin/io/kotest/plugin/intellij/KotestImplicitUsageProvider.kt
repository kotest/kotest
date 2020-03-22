package io.kotest.plugin.intellij

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.styles.isAnySpecSubclass
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotestImplicitUsageProvider : ImplicitUsageProvider {

  override fun isImplicitWrite(element: PsiElement?): Boolean = false
  override fun isImplicitRead(element: PsiElement?): Boolean = false

  override fun isImplicitUsage(element: PsiElement?): Boolean {
    val ktclass = when (element) {
      is KtClassOrObject -> element
      is KtLightClass -> element.kotlinOrigin
      else -> null
    }
    return ktclass?.isAnySpecSubclass() ?: false
  }
}