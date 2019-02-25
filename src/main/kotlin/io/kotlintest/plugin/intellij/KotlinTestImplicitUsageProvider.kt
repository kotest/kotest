package io.kotlintest.plugin.intellij

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiElement
import io.kotlintest.plugin.intellij.psi.isSpec
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.psi.KtClassOrObject

class KotlinTestImplicitUsageProvider : ImplicitUsageProvider {

  override fun isImplicitWrite(element: PsiElement?): Boolean = false
  override fun isImplicitRead(element: PsiElement?): Boolean = false

  override fun isImplicitUsage(element: PsiElement?): Boolean {
    val ktclass = when (element) {
      is KtClassOrObject -> element
      is KtLightClass -> element.kotlinOrigin
      else -> null
    }
    return ktclass?.isSpec() ?: false
  }
}