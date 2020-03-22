package io.kotest.plugin.intellij

import com.intellij.ide.fileTemplates.FileTemplateDescriptor
import com.intellij.lang.Language
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.testIntegration.JavaTestFramework
import org.jetbrains.kotlin.idea.KotlinLanguage
import javax.swing.Icon

class KotestTestFramework : JavaTestFramework() {

   private val specs = listOf(
       "io.kotest.core.spec.style.BehaviorSpec",
       "io.kotest.core.spec.style.DescribeSpec",
       "io.kotest.core.spec.style.ExpectSpec",
       "io.kotest.core.spec.style.FeatureSpec",
       "io.kotest.core.spec.style.FreeSpec",
       "io.kotest.core.spec.style.FunSpec",
       "io.kotest.core.spec.style.ShouldSpec",
       "io.kotest.core.spec.style.StringSpec",
       "io.kotest.core.spec.style.WordSpec"
   )

   override fun getName(): String = "Kotest"
   override fun getLanguage(): Language = KotlinLanguage.INSTANCE
   override fun getIcon(): Icon = Icons.Kotest16

   override fun findOrCreateSetUpMethod(clazz: PsiClass?): PsiMethod? = null
   override fun findSetUpMethod(clazz: PsiClass): PsiMethod? = null
   override fun findTearDownMethod(clazz: PsiClass): PsiMethod? = null

   override fun isTestClass(clazz: PsiClass, canBePotential: Boolean): Boolean {
      return if (canBePotential) isUnderTestSources(clazz) else clazz.superTypes.any { specs.contains(it.resolve()?.qualifiedName) }
   }

   override fun getMarkerClassFQName(): String = "io.kotest.core.test.TestCase"

   override fun getMnemonic(): Char = 'K'
   override fun getLibraryPath(): String? = null

   override fun isLibraryAttached(module: Module): Boolean = true

   override fun getDefaultSuperClass(): String = "io.kotest.core.spec.style.StringSpec"

   override fun isPotentialTestClass(clazz: PsiElement): Boolean {
      val psiFile = clazz.containingFile
      val vFile = psiFile.virtualFile ?: return false
      return ProjectRootManager.getInstance(clazz.project).fileIndex.isInTestSourceContent(vFile)
   }

   override fun getSetUpMethodFileTemplateDescriptor(): FileTemplateDescriptor? = null
   override fun getTearDownMethodFileTemplateDescriptor(): FileTemplateDescriptor? = null

   // we don't use this but it's marked as non null so must return it
   override fun getTestMethodFileTemplateDescriptor(): FileTemplateDescriptor = FileTemplateDescriptor("unused")

   override fun isTestMethod(element: PsiElement?): Boolean = false
}