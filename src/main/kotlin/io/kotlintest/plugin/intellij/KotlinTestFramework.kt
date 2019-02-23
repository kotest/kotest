package io.kotlintest.plugin.intellij

import com.intellij.ide.fileTemplates.FileTemplateDescriptor
import com.intellij.lang.Language
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ExternalLibraryDescriptor
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.testIntegration.JavaTestFramework
import org.jetbrains.kotlin.idea.KotlinLanguage
import javax.swing.Icon

class KotlinTestFramework : JavaTestFramework() {

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

  override fun getFrameworkLibraryDescriptor(): ExternalLibraryDescriptor {
    return ExternalLibraryDescriptor("io.kotlintest", "kotlintest-core", "3.0.0", "3.99999.99999", "3.2.1")
  }

  override fun findSetUpMethod(clazz: PsiClass): PsiMethod? = null
  override fun findTearDownMethod(clazz: PsiClass): PsiMethod? = null

  override fun isTestClass(clazz: PsiClass, canBePotential: Boolean): Boolean {
    return if (canBePotential) isUnderTestSources(clazz) else clazz.superTypes.any { specs.contains(it.className) }
  }

  override fun isLibraryAttached(module: Module): Boolean = true

  override fun findOrCreateSetUpMethod(clazz: PsiClass): PsiMethod? {
    val manager = clazz.manager
    val factory = JavaPsiFacade.getInstance(manager.project).elementFactory
    return factory.createMethod("beforeTest", null)
  }

  // used to see if the library is the classpath
  override fun getMarkerClassFQName(): String = "io.kotlintest.TestCase"

  override fun getMnemonic(): Char = 'K'

  override fun getIcon(): Icon = Icons.KotlinTest16
  override fun getDefaultSuperClass(): String = "io.kotlintest.specs.StringSpec"

  override fun isPotentialTestClass(clazz: PsiElement): Boolean {
    val psiFile = clazz.containingFile
    val vFile = psiFile.virtualFile ?: return false
    return ProjectRootManager.getInstance(clazz.project).fileIndex.isInTestSourceContent(vFile)
  }

  override fun getName(): String {
    return "KotlinTest"
  }

  override fun getLanguage(): Language {
    return KotlinLanguage.INSTANCE
  }

  override fun getSetUpMethodFileTemplateDescriptor(): FileTemplateDescriptor {
    return FileTemplateDescriptor("KotlinTestBeforeTest.java")
  }

  override fun getTearDownMethodFileTemplateDescriptor(): FileTemplateDescriptor {
    return FileTemplateDescriptor("KotlinTestAfterTest.java")
  }

  override fun getTestMethodFileTemplateDescriptor(): FileTemplateDescriptor {
    return FileTemplateDescriptor("KotlinTestTestCase.java")
  }

  override fun isTestMethod(element: PsiElement, checkAbstract: Boolean): Boolean {
    return false
  }
}