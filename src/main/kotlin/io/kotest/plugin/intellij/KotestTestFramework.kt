package io.kotest.plugin.intellij

import com.intellij.ide.fileTemplates.FileTemplateDescriptor
import com.intellij.lang.Language
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testIntegration.TestFramework
import io.kotest.plugin.intellij.psi.isContainedInSpec
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import javax.swing.Icon

class KotestTestFramework : TestFramework {

   override fun getDefaultSuperClass(): String = "io.kotest.core.spec.style.FunSpec"

   private fun isUnderTestSources(clazz: PsiClass): Boolean {
      val psiFile = clazz.containingFile
      val vFile = psiFile.virtualFile ?: return false
      return ProjectRootManager.getInstance(clazz.project).fileIndex.isInTestSourceContent(vFile)
   }

   override fun isPotentialTestClass(clazz: PsiElement): Boolean {
      return clazz is PsiClass && isUnderTestSources(clazz)
   }

   override fun isTestClass(clazz: PsiElement): Boolean {
      return clazz is PsiClass && clazz.isContainedInSpec()
   }

   override fun getName(): String = "Kotest"
   override fun getLanguage(): Language = KotlinLanguage.INSTANCE
   override fun getIcon(): Icon = Icons.Kotest16

   override fun getLibraryPath(): String? = null

   override fun findOrCreateSetUpMethod(clazz: PsiElement): PsiElement? = null

   override fun isIgnoredMethod(element: PsiElement?): Boolean = false

   override fun findSetUpMethod(clazz: PsiElement): PsiElement? =
      findBeforeTestBlock(clazz) ?: findBeforeTestFunction(clazz)

   override fun findTearDownMethod(clazz: PsiElement): PsiElement? =
      findAfterTestBlock(clazz) ?: findAfterTestFunction(clazz)

   private fun findBeforeTestBlock(clazz: PsiElement): PsiElement? {
      return clazz.getChildrenOfType<KtNameReferenceExpression>().firstOrNull { it.text == "beforeTest" }
   }

   private fun findAfterTestBlock(clazz: PsiElement): PsiElement? {
      return clazz.getChildrenOfType<KtNameReferenceExpression>().firstOrNull { it.text == "afterTest" }
   }

   private fun findBeforeTestFunction(clazz: PsiElement): PsiElement? {
      return clazz.getChildrenOfType<KtNamedFunction>()
         .filter { it.name == "beforeTest" }
         .firstOrNull { it.valueParameters.size == 1 }
   }

   private fun findAfterTestFunction(clazz: PsiElement): PsiElement? {
      return clazz.getChildrenOfType<KtNamedFunction>()
         .filter { it.name == "afterTest" }
         .firstOrNull { it.valueParameters.size == 1 }
   }

   override fun isTestMethod(element: PsiElement?): Boolean = false

   override fun getSetUpMethodFileTemplateDescriptor(): FileTemplateDescriptor? = null
   override fun getTearDownMethodFileTemplateDescriptor(): FileTemplateDescriptor? = null

   // we don't use this but it's marked as non null so must return it
   override fun getTestMethodFileTemplateDescriptor(): FileTemplateDescriptor = FileTemplateDescriptor("unused")

   override fun isLibraryAttached(module: Module): Boolean {
      val scope = GlobalSearchScope.allScope(module.project)
      val c = JavaPsiFacade.getInstance(module.project).findClass(defaultSuperClass, scope)
      return c != null
   }
}
