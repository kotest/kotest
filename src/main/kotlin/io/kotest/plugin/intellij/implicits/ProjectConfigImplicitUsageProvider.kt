package io.kotest.plugin.intellij.implicits

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.getSuperClassSimpleName
import io.kotest.plugin.intellij.psi.toKtClass
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.psi.KtClass

/**
 * Allows to disable highlighting of certain elements as unused when such elements are not referenced
 * from the code but are referenced in some other way.
 *
 * This [ImplicitUsageProvider] will mark project config classes as used.
 */
class ProjectConfigImplicitUsageProvider : ImplicitUsageProvider {

   override fun isImplicitWrite(element: PsiElement): Boolean = false
   override fun isImplicitRead(element: PsiElement): Boolean = false

   /**
    * Marks subclasses of AbstractProjectConfig as used.
    */
   override fun isImplicitUsage(element: PsiElement): Boolean {
      val parent = when (element) {
         is KtClass -> element.getSuperClassSimpleName()
         is KtLightClass -> element.toKtClass()?.getSuperClassSimpleName()
         else -> null
      }
      return parent == "AbstractProjectConfig"
   }
}
