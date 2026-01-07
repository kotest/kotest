package io.kotest.plugin.intellij.implicits

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Allows to disable highlighting of certain elements as unused when such elements are not referenced
 * from the code but are referenced in some other way.
 *
 * This [ImplicitUsageProvider] will mark classes that are annotated with @AutoScan as implicitly used.
 */
@Deprecated("AutoScan has been removed from Kotest since 6.0")
class AutoScanUsageProvider : ImplicitUsageProvider {

   override fun isImplicitWrite(element: PsiElement): Boolean = false
   override fun isImplicitRead(element: PsiElement): Boolean = false

   override fun isImplicitUsage(element: PsiElement): Boolean {
      val ktclass = when (element) {
         is KtClass -> element
         is KtLightClass -> when (val origin = element.kotlinOrigin) {
            is KtClassOrObject -> origin
            else -> null
         }
         else -> null
      }
      return ktclass?.annotationEntries?.any { it.text == "@AutoScan" } ?: false
   }
}
