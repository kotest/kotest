package io.kotest.plugin.intellij.locations

import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.ClassUtil
import org.jetbrains.kotlin.idea.stubindex.KotlinFullClassNameIndex
import org.jetbrains.kotlin.psi.KtClassOrObject

class MultiplatformJavaSuiteLocator : SMTestLocator, DumbAware {

   override fun getLocation(
      protocol: String,
      path: String,
      project: Project,
      scope: GlobalSearchScope
   ): List<Location<PsiElement>> {
      if (DumbService.isDumb(project)) return emptyList()
      return findJvmLocation(project, path) ?: findKmpLocations(project, path)
   }

   private fun findJvmLocation(project: Project, path: String): List<Location<PsiElement>>? {
      val psiClass: PsiClass? = ClassUtil.findPsiClass(
         /* manager = */ PsiManager.getInstance(project),
         /* name = */ path,
      )
      return if (psiClass == null) null else listOf(createPsiClassNavigable(psiClass))
   }

   private fun findKmpLocations(project: Project, path: String): List<Location<PsiElement>> {
      val ktclasses = KotlinFullClassNameIndex[path, project, GlobalSearchScope.allScope(project)]
      return ktclasses.map { createKtClassNavigable(it) }
   }

   private fun createKtClassNavigable(ktClassOrObject: KtClassOrObject): Location<PsiElement> {
      return PsiLocation(ktClassOrObject.project, ktClassOrObject)
   }

   private fun createPsiClassNavigable(psiClass: PsiClass): Location<PsiElement> {
      return PsiLocation(psiClass.project, psiClass)
   }
}
