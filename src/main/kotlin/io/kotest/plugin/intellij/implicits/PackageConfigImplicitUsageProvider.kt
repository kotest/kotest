package io.kotest.plugin.intellij.implicits

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.isSubclass
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * Allows disabling highlighting of certain elements as unused when such elements are not referenced
 * from the code but are referenced in some other way.
 *
 * This [PackageConfigImplicitUsageProvider] will mark module config classes as used.
 */
class PackageConfigImplicitUsageProvider : ImplicitUsageProvider {

   private val fqn = FqName("io.kotest.core.config.AbstractPackageConfig")

   override fun isImplicitWrite(element: PsiElement): Boolean = false
   override fun isImplicitRead(element: PsiElement): Boolean = false

   /**
    * Marks subclasses of AbstractProjectConfig as used.
    */
   override fun isImplicitUsage(element: PsiElement): Boolean {
      return when (element) {
         is KtClassOrObject -> element.isSubclass(fqn)
         is KtLightClass -> {
            val o = element.kotlinOrigin ?: return false
            return when (o) {
               is KtObjectDeclaration -> o.isSubclass(fqn)
               is KtClass -> o.isSubclass(fqn)
               else -> false
            }
         }
         else -> false
      }
   }
}
