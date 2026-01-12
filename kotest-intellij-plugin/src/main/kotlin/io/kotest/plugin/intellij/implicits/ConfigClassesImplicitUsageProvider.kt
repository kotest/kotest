package io.kotest.plugin.intellij.implicits

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.immediateSuperClasses
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Allows disabling highlighting of certain elements as unused when such elements are not referenced
 * from the code but are referenced in some other way.
 *
 * This [ImplicitUsageProvider] will mark project and package config classes as used.
 */
class ConfigClassesImplicitUsageProvider : ImplicitUsageProvider {

   private val projectConfigFQN = FqName("io.kotest.core.config.AbstractProjectConfig")
   private val packageConfigFQN = FqName("io.kotest.core.config.AbstractPackageConfig")

   override fun isImplicitWrite(element: PsiElement): Boolean = false
   override fun isImplicitRead(element: PsiElement): Boolean = false

   /**
    * Marks subclasses of AbstractProjectConfig as used.
    */
   override fun isImplicitUsage(element: PsiElement): Boolean {
      return when (element) {
         is KtClassOrObject -> isConfigClass(element)
         else -> when (element.navigationElement) {
            is KtClassOrObject -> isConfigClass(element.navigationElement as KtClassOrObject)
            else -> false
         }
      }
   }

   private fun isConfigClass(ktclass: KtClassOrObject): Boolean {
      val supers = ktclass.immediateSuperClasses()
     return supers.contains(projectConfigFQN) || supers.contains(packageConfigFQN)
   }
}
