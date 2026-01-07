package io.kotest.plugin.intellij.implicits

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.isSpec
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.asJava.classes.KtUltraLightClass
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Allows disabling highlighting of certain elements as unused when such elements are not referenced
 * from the code but are referenced in some other way.
 *
 * This [ImplicitUsageProvider] will mark spec classes / objects as used, because a test class
 * is never referenced by anything but is used.
 */
class SpecImplicitUsageProvider : ImplicitUsageProvider {

   override fun isImplicitWrite(element: PsiElement): Boolean = false
   override fun isImplicitRead(element: PsiElement): Boolean = false

   override fun isImplicitUsage(element: PsiElement): Boolean {
      val ktclass = when (element) {
         is KtClassOrObject -> element
         is KtUltraLightClass -> element
         is KtLightClass -> when (val origin = element.kotlinOrigin) {
            is KtClassOrObject -> origin
            else -> null
         }
         else -> null
      }
      return ktclass?.isSpec() == true
   }
}
