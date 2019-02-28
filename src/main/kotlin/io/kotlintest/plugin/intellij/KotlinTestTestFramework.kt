package io.kotlintest.plugin.intellij

import com.intellij.ide.fileTemplates.FileTemplateDescriptor
import com.intellij.lang.Language
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ExternalLibraryDescriptor
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.testIntegration.JavaTestFramework
import org.jetbrains.kotlin.idea.KotlinLanguage
import javax.swing.Icon

class KotlinTestTestFramework : JavaTestFramework() {

  private val specs = listOf(
      "io.kotlintest.specs.BehaviorSpec",
      "io.kotlintest.specs.DescribeSpec",
      "io.kotlintest.specs.ExpectSpec",
      "io.kotlintest.specs.FeatureSpec",
      "io.kotlintest.specs.FreeSpec",
      "io.kotlintest.specs.FunSpec",
      "io.kotlintest.specs.ShouldSpec",
      "io.kotlintest.specs.StringSpec",
      "io.kotlintest.specs.WordSpec"
  )

  override fun getName(): String = "KotlinTest"
  override fun getLanguage(): Language = KotlinLanguage.INSTANCE
  override fun getIcon(): Icon = Icons.KotlinTest16

  override fun getFrameworkLibraryDescriptor(): ExternalLibraryDescriptor {
    return ExternalLibraryDescriptor("io.kotlintest", "kotlintest-core", "3.0.0", "3.99999.99999", "3.2.1")
  }

  override fun findOrCreateSetUpMethod(clazz: PsiClass?): PsiMethod? = null
  override fun findSetUpMethod(clazz: PsiClass): PsiMethod? = null
  override fun findTearDownMethod(clazz: PsiClass): PsiMethod? = null

  override fun isTestClass(clazz: PsiClass, canBePotential: Boolean): Boolean {
    return if (canBePotential) isUnderTestSources(clazz) else clazz.superTypes.any { specs.contains(it.resolve()?.qualifiedName) }
  }

  override fun getMarkerClassFQName(): String = "io.kotlintest.TestCase"

  override fun getMnemonic(): Char = 'K'
  override fun getLibraryPath(): String? = null

  override fun isLibraryAttached(module: Module): Boolean = true

  override fun getDefaultSuperClass(): String = "io.kotlintest.specs.StringSpec"

  override fun isPotentialTestClass(clazz: PsiElement): Boolean {
    val psiFile = clazz.containingFile
    val vFile = psiFile.virtualFile ?: return false
    return ProjectRootManager.getInstance(clazz.project).fileIndex.isInTestSourceContent(vFile)
  }

  override fun getSetUpMethodFileTemplateDescriptor(): FileTemplateDescriptor? = null
  override fun getTearDownMethodFileTemplateDescriptor(): FileTemplateDescriptor? = null

  // we don't use this but it's marked as non null so must return it
  override fun getTestMethodFileTemplateDescriptor(): FileTemplateDescriptor = FileTemplateDescriptor("unused")

  override fun isTestMethod(element: PsiElement, checkAbstract: Boolean): Boolean = false
}